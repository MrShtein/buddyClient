package mr.shtein.buddyandroidclient.presentation.presenter

import kotlinx.coroutines.*
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractor
import mr.shtein.buddyandroidclient.domain.interactor.LocationInteractor
import mr.shtein.buddyandroidclient.exceptions.validate.LocationServiceException
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.Coordinates
import mr.shtein.buddyandroidclient.model.LocationState
import mr.shtein.buddyandroidclient.model.dto.AnimalFilter
import mr.shtein.buddyandroidclient.presentation.screen.*
import mr.shtein.buddyandroidclient.utils.FragmentsListForAssigningAnimation
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.math.floor

const val DOG_ID: Int = 1
const val CAT_ID: Int = 2
private const val ANIMAL_CARD_LABEL = "AnimalsCardFragment"
private const val KENNEL_LABEL = "AddKennelFragment"
private const val USER_PROFILE_LABEL = "UserProfileFragment"
private const val REGISTRATION_LABEL = "UserRegistrationFragment"
private const val LOGIN_LABEL = "LoginFragment"

interface AnimalListPresenter {
    fun onAnimalShowCommand(
        isDogChecked: Boolean,
        isCatChecked: Boolean,
        getFromNetwork: Boolean = true
    )

    fun onAttachView(view: AnimalListView)
    fun onDetachView()
    fun onClickToLocationBtn(permissions: Map<String, Boolean>)
    fun onUpdatedList(newAnimalList: List<Animal>, previousListSize: Int)

    fun onChangeAnimationsWhenNavigate(fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation)
    fun onChangeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation?)
    fun onSetupView()
    fun onGetListForAssigningAnimation(destination: String): FragmentsListForAssigningAnimation
}

class AnimalsListPresenterImpl(
    private val animalInteractor: AnimalInteractor,
    private val locationService: LocationInteractor,
    private val userPropertiesRepository: UserPropertiesRepository,
    private val mainDispatchers: CoroutineDispatcher = Dispatchers.Main
) : AnimalListPresenter {

    private val coroutine: CoroutineScope = CoroutineScope(mainDispatchers)
    private var animalListView: AnimalListView? = null
    private var animalList: List<Animal>? = null
    private var locationList: HashMap<Int, Int>? = null
    private var locationState: LocationState = LocationState.INIT_STATE
    private var isUiMustUpdate = false
    private var locationIsAllowed: Boolean? = false

    override fun onAnimalShowCommand(
        isDogChecked: Boolean,
        isCatChecked: Boolean,
        getFromNetwork: Boolean
    ) {
        animalListView?.showAnimalSearchProgressBar()
        if (!getFromNetwork && animalList != null) {
            pendingOrUpdateAnimalList(animalListView)
            animalListView?.hideAnimalSearchProgressBar()
            return
        }
        if (isUiMustUpdate) {
            animalListView?.updateList(animalList!!)
            isUiMustUpdate = false
            animalListView?.hideAnimalSearchProgressBar()
            return
        }
        val locationIsAllowed = animalListView?.checkLocationPermission()
        coroutine.launch {
            try {
                val animalFilter: AnimalFilter = makeAnimalFilter(isDogChecked, isCatChecked)
                animalList = animalInteractor.getAnimalsByFilter(animalFilter.animalTypeId)
                if (locationIsAllowed == true) {
                    locationState = LocationState.SEARCH_STATE
                    animalList = changeLocationState(locationState)
                    pendingOrUpdateAnimalList(animalListView)
                    val token: String = userPropertiesRepository.getUserToken()
                    val coordinates = locationService.getCurrentDistance()
                    successLocation(token, coordinates)
                    return@launch
                }
                animalList?.let {
                    animalList = changeLocationState(locationState)
                    pendingOrUpdateAnimalList(animalListView)
                }

            } catch (ex: ConnectException) {
                animalListView?.showError(R.string.internet_failure_text)
            } catch (ex: SocketTimeoutException) {
                animalListView?.showError(R.string.internet_failure_text)
            } catch (ex: ServerErrorException) {
                animalListView?.showError(R.string.server_error_msg)
            } catch (ex: LocationServiceException) {
                animalListView?.showError(R.string.server_error_msg) //TODO Add text
            } finally {
                animalListView?.hideAnimalSearchProgressBar()
            }
        }
    }

    private fun pendingOrUpdateAnimalList(animalListView: AnimalListView?) {
        if (animalListView == null) {
            isUiMustUpdate = true
        } else {
            animalListView.updateList(animalList!!)
        }
    }

    override fun onClickToLocationBtn(permissions: Map<String, Boolean>) {
        if (permissions.containsValue(true)) {
            val animalsWithNewState = changeLocationState(LocationState.SEARCH_STATE)
            animalListView?.updateList(animalsWithNewState)
            coroutine.launch {
                try {
                    val coordinates: Coordinates = locationService.getCurrentDistance()
                    val token: String = userPropertiesRepository.getUserToken()
                    successLocation(token, coordinates)
                } catch (ex: Exception) {
                    failureLocation()
                }
            }
        } else {
            animalListView?.showLocationFailureText(R.string.location_failure_text)
        }
    }

    override fun onUpdatedList(newAnimalList: List<Animal>, previousListSize: Int) {
        animalList = newAnimalList
        if (previousListSize != animalList?.size) {
            animalListView?.showAnimalCountText(animalList?.size!!)
        }
    }

    private fun successLocation(token: String, coordinates: Coordinates) {
        coroutine.launch {
            try {
                locationList = animalInteractor.getDistancesFromUser(token, coordinates)
                locationState = LocationState.DISTANCE_VISIBLE_STATE
                animalList = setDistancesToAnimals(locationList!!)
                animalList = changeLocationState(locationState)
            } catch (ex: ConnectException) {
                animalListView?.showError(R.string.internet_failure_text)
                locationState = LocationState.BAD_RESULT_STATE
                changeLocationState(locationState)
            } catch (ex: SocketTimeoutException) {
                animalListView?.showError(R.string.internet_failure_text)
                locationState = LocationState.BAD_RESULT_STATE
                changeLocationState(locationState)
            } catch (ex: ServerErrorException) {
                animalListView?.showError(R.string.server_error_msg)
                locationState = LocationState.BAD_RESULT_STATE
                changeLocationState(locationState)
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                pendingOrUpdateAnimalList(animalListView)
            }
        }
    }

    private fun failureLocation() {
        locationState = LocationState.BAD_RESULT_STATE
        animalList = changeLocationState(locationState)
        animalListView?.showError(R.string.location_failure_text)
        pendingOrUpdateAnimalList(animalListView)
    }

    override fun onAttachView(view: AnimalListView) {
        animalListView = view
        animalListView?.setUpView()
    }

    override fun onDetachView() {
        animalListView = null
    }

    private fun changeLocationState(state: LocationState): List<Animal> {
        locationState = state
        val newAnimalList = mutableListOf<Animal>()
        animalList?.forEach { animal ->
            val newAnimal = animal.copy()
            newAnimal.locationState = state
            newAnimalList.add(newAnimal)
        }
        return newAnimalList.toList()
    }

    private fun makeAnimalFilter(
        isDogChecked: Boolean,
        isCatChecked: Boolean
    ): AnimalFilter {
        val listForFilter: MutableList<Int> = mutableListOf()
        if (isDogChecked) listForFilter.add(DOG_ID)
        if (isCatChecked) listForFilter.add(CAT_ID)
        return AnimalFilter(listForFilter.toList())
    }

    private fun setDistancesToAnimals(distances: HashMap<Int, Int>): List<Animal> {
        val newAnimalList = mutableListOf<Animal>()
        animalList?.forEach { animal ->
            val newAnimal = animal.copy()
            newAnimalList.add(newAnimal)
            val kennel = animal.kennel
            val distance = distances[kennel.id] ?: 0
            distance.let {
                if (distance < 1000) {
                    newAnimal.distance = "$distance м. от Вас"
                } else {
                    val distanceInKm = floor(distance.toDouble() / 100) / 10
                    newAnimal.distance = "$distanceInKm км. от Вас"
                }
            }
        }
        return newAnimalList
    }

    override fun onChangeAnimationsWhenNavigate(fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation) {
        when (fragmentsListForAssigningAnimation) {
            FragmentsListForAssigningAnimation.ANIMAL_CARD -> {
                animalListView?.setAnimationWhenToAnimalCardNavigate()
            }
            FragmentsListForAssigningAnimation.ADD_KENNEL -> {
                animalListView?.setAnimationWhenToAddKennelNavigate()
            }
            FragmentsListForAssigningAnimation.USER_PROFILE -> {
                animalListView?.setAnimationWhenToUserProfileNavigate()
            }
            else -> {
                animalListView?.setAnimationWhenToOtherFragmentNavigate()
            }
        }
    }

    override fun onChangeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation?) {
        when (fragmentsListForAssigningAnimation) {
            FragmentsListForAssigningAnimation.ADD_KENNEL -> {
                animalListView?.setAnimationWhenUserComeFromAddKennel()
            }
            FragmentsListForAssigningAnimation.USER_PROFILE -> {
                animalListView?.setAnimationWhenUserComeFromUserProfile()
            }
            FragmentsListForAssigningAnimation.LOGIN -> {
                animalListView?.setAnimationWhenUserComeFromLogin()
            }
            FragmentsListForAssigningAnimation.CITY_CHOICE -> {
                animalListView?.setAnimationWhenUserComeFromCity()
            }
            FragmentsListForAssigningAnimation.START -> {
                animalListView?.setAnimationWhenUserComeFromSplash()
            }
        }
    }

    override fun onGetListForAssigningAnimation(destination: String): FragmentsListForAssigningAnimation {
        return when (destination) {
            ANIMAL_CARD_LABEL -> FragmentsListForAssigningAnimation.ANIMAL_CARD
            KENNEL_LABEL -> FragmentsListForAssigningAnimation.ADD_KENNEL
            USER_PROFILE_LABEL -> FragmentsListForAssigningAnimation.USER_PROFILE
            REGISTRATION_LABEL -> FragmentsListForAssigningAnimation.REGISTRATION
            LOGIN_LABEL -> FragmentsListForAssigningAnimation.LOGIN
            else -> FragmentsListForAssigningAnimation.OTHER
        }
    }

    override fun onSetupView() {

    }


}