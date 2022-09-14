package mr.shtein.buddyandroidclient.presentation.presenter

import android.content.pm.PackageManager
import kotlinx.coroutines.*
import moxy.InjectViewState
import moxy.MvpPresenter
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.domain.interactor.AnimalFilterInteractor
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
import kotlin.jvm.Throws
import kotlin.math.floor

const val DOG_ID: Int = 1
const val CAT_ID: Int = 2
private const val ANIMAL_CARD_LABEL = "AnimalsCardFragment"
private const val KENNEL_LABEL = "AddKennelFragment"
private const val USER_PROFILE_LABEL = "UserProfileFragment"
private const val REGISTRATION_LABEL = "UserRegistrationFragment"
private const val LOGIN_LABEL = "LoginFragment"
private const val ANIMAL_FILTER_LABEL = "AnimalFilterFragment"


interface AnimalListPresenter {
    fun onAnimalShowCommand(getFromNetwork: Boolean = true)
    fun onClickToLocationBtn(permissions: Map<String, Boolean>)
    fun onUpdatedList(newAnimalList: List<Animal>, previousListSize: Int)
    fun onChangeAnimationsWhenNavigate(fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation)
    fun onChangeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation?)
    fun onGetListForAssigningAnimation(destination: String): FragmentsListForAssigningAnimation
    fun onInit(
        fineLocationPermission: Int,
        coarseLocationPermission: Int,
        animalFilter: AnimalFilter
    )

    fun onDogChipClicked(dogChecked: Boolean)
    fun onCatChipClicked(catChecked: Boolean)
    fun onChipsReadyToToggle()
}

@InjectViewState
class AnimalsListPresenterImpl(
    private val animalInteractor: AnimalInteractor,
    private val filterInteractor: AnimalFilterInteractor,
    private val locationService: LocationInteractor,
    private val userPropertiesRepository: UserPropertiesRepository,
    private val mainDispatchers: CoroutineDispatcher = Dispatchers.Main
) : MvpPresenter<AnimalListView>(), AnimalListPresenter {

    private var animalList: List<Animal>? = null
    private var locationList: HashMap<Int, Int>? = null
    private var locationState: LocationState = LocationState.INIT_STATE
    private var fineLocationPermission: Int = PackageManager.PERMISSION_DENIED
    private var coarseLocationPermission: Int = PackageManager.PERMISSION_DENIED
    private lateinit var animalFilter: AnimalFilter
    private var couroutineScope: CoroutineScope = CoroutineScope(mainDispatchers)

    override fun onAnimalShowCommand(
        getFromNetwork: Boolean
    ) {
        viewState.toggleAnimalSearchProgressBar(isVisible = true)
        if (!getFromNetwork && animalList != null) { // в случае если пользователь нажал на фильтр animalList != null, но нам необходимы данные из сети!!!
            viewState.updateList(animalList!!)
            viewState.toggleAnimalSearchProgressBar(isVisible = false)
            return
        }
        couroutineScope.launch {
            try {
                animalList = animalInteractor.getAnimalsByFilter(animalFilter)
                if (fineLocationPermission == PackageManager.PERMISSION_GRANTED
                    || coarseLocationPermission == PackageManager.PERMISSION_GRANTED
                ) {
                    locationState = LocationState.SEARCH_STATE
                    animalList = changeLocationState(locationState)
                    viewState.updateList(animalList!!)
                    val token: String = userPropertiesRepository.getUserToken()
                    val coordinates = locationService.getCurrentDistance()
                    successLocation(token, coordinates)
                } else {
                    animalList?.let {
                        animalList = changeLocationState(locationState)
                        viewState.updateList(animalList!!)
                    }
                }
            } catch (ex: ConnectException) {
                viewState.showError(R.string.internet_failure_text)
            } catch (ex: SocketTimeoutException) {
                viewState.showError(R.string.internet_failure_text)
            } catch (ex: ServerErrorException) {
                viewState.showError(R.string.server_error_msg)
            } catch (ex: LocationServiceException) {
                viewState.showError(R.string.location_failure_text)
            } finally {
                viewState.toggleAnimalSearchProgressBar(isVisible = false)
                if (locationState == LocationState.SEARCH_STATE) failureLocation()
            }
        }
    }

    override fun onClickToLocationBtn(permissions: Map<String, Boolean>) {
        if (permissions.containsValue(true)) {
            val animalsWithNewState = changeLocationState(LocationState.SEARCH_STATE)
            viewState.updateList(animalsWithNewState)
            couroutineScope.launch {
                try {
                    val coordinates: Coordinates = locationService.getCurrentDistance()
                    val token: String = userPropertiesRepository.getUserToken()
                    successLocation(token, coordinates)
                } catch (ex: Exception) {
                    failureLocation()
                }
            }
        } else {
            viewState.showLocationFailureText(R.string.location_failure_text)
        }
    }

    override fun onUpdatedList(newAnimalList: List<Animal>, previousListSize: Int) {
        animalList = newAnimalList
        if (previousListSize != animalList?.size) {
            viewState.showAnimalCountText(animalList?.size!!)
        }
    }

    private suspend fun successLocation(token: String, coordinates: Coordinates) {
        locationList = animalInteractor.getDistancesFromUser(token, coordinates)
        locationState = LocationState.DISTANCE_VISIBLE_STATE
        animalList = setDistancesToAnimals(locationList!!)
        animalList = changeLocationState(locationState)
        viewState.updateList(animalList!!)
    }

    private fun failureLocation() {
        locationState = LocationState.BAD_RESULT_STATE
        animalList = changeLocationState(locationState)
        viewState.showError(R.string.location_failure_text)
        viewState.updateList(animalList!!)
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
                viewState.setAnimationWhenToAnimalCardNavigate()
            }

            FragmentsListForAssigningAnimation.ADD_KENNEL -> {
                viewState.setAnimationWhenToAddKennelNavigate()
            }

            FragmentsListForAssigningAnimation.USER_PROFILE -> {
                viewState.setAnimationWhenToUserProfileNavigate()
            }

            FragmentsListForAssigningAnimation.ANIMAL_FILTER -> {
                viewState.setAnimationWhenToAnimalFilterNavigate()
            }

            else -> {
                viewState.setAnimationWhenToOtherFragmentNavigate()
            }
        }
    }

    override fun onChangeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation?) {
        when (fragmentsListForAssigningAnimation) {
            FragmentsListForAssigningAnimation.ADD_KENNEL -> {
                viewState.setAnimationWhenUserComeFromAddKennel()
            }

            FragmentsListForAssigningAnimation.USER_PROFILE -> {
                viewState.setAnimationWhenUserComeFromUserProfile()
            }

            FragmentsListForAssigningAnimation.LOGIN -> {
                viewState.setAnimationWhenUserComeFromLogin()
            }

            FragmentsListForAssigningAnimation.CITY_CHOICE -> {
                viewState.setAnimationWhenUserComeFromCity()
            }

            FragmentsListForAssigningAnimation.START -> {
                viewState.setAnimationWhenUserComeFromSplash()
            }

            else -> {}
        }
    }

    override fun onGetListForAssigningAnimation(destination: String): FragmentsListForAssigningAnimation {
        return when (destination) {
            ANIMAL_CARD_LABEL -> FragmentsListForAssigningAnimation.ANIMAL_CARD
            KENNEL_LABEL -> FragmentsListForAssigningAnimation.ADD_KENNEL
            USER_PROFILE_LABEL -> FragmentsListForAssigningAnimation.USER_PROFILE
            REGISTRATION_LABEL -> FragmentsListForAssigningAnimation.REGISTRATION
            LOGIN_LABEL -> FragmentsListForAssigningAnimation.LOGIN
            ANIMAL_FILTER_LABEL -> FragmentsListForAssigningAnimation.ANIMAL_FILTER
            else -> FragmentsListForAssigningAnimation.OTHER
        }
    }

    override fun onInit(
        fineLocationPermission: Int,
        coarseLocationPermission: Int,
        animalFilter: AnimalFilter
    ) {
        this.fineLocationPermission = fineLocationPermission
        this.coarseLocationPermission = coarseLocationPermission
        this.animalFilter = animalFilter
    }

    override fun onDogChipClicked(dogChecked: Boolean) {
        val animalTypeId: MutableList<Int> = animalFilter.animalTypeId ?: mutableListOf()
        if (dogChecked) {
            animalTypeId.add(DOG_ID)
            animalFilter.animalTypeId = animalTypeId
            val animalTypeList: MutableList<Int> =
                filterInteractor.getAnimalTypeId() ?: mutableListOf()
            animalTypeList.add(DOG_ID)
            filterInteractor.saveAnimalTypeId(animalTypeList)
        } else {
            animalTypeId.remove(DOG_ID)
            animalFilter.animalTypeId = animalTypeId
            val animalTypeList: MutableList<Int> =
                filterInteractor.getAnimalTypeId() ?: mutableListOf()
            animalTypeList.remove(DOG_ID)
            filterInteractor.saveAnimalTypeId(animalTypeList)
        }
    }

    override fun onCatChipClicked(catChecked: Boolean) {
        val animalTypeId: MutableList<Int> = animalFilter.animalTypeId ?: mutableListOf()
        if (catChecked) {
            animalTypeId.add(CAT_ID)
            animalFilter.animalTypeId = animalTypeId
            val animalTypeList: MutableList<Int> =
                filterInteractor.getAnimalTypeId() ?: mutableListOf()
            animalTypeList.add(CAT_ID)
            filterInteractor.saveAnimalTypeId(animalTypeList)
        } else {
            animalTypeId.remove(CAT_ID)
            animalFilter.animalTypeId = animalTypeId
            val animalTypeList: MutableList<Int> =
                filterInteractor.getAnimalTypeId() ?: mutableListOf()
            animalTypeList.remove(CAT_ID)
            filterInteractor.saveAnimalTypeId(animalTypeList)
        }
    }

    override fun onChipsReadyToToggle() {
        val animalTypeIdList = animalFilter.animalTypeId
        animalTypeIdList?.let {
            if (it.contains(DOG_ID)) viewState.toggleDogChip(isChecked = true)
            if (it.contains(CAT_ID)) viewState.toggleCatChip(isChecked = true)
        }

    }

    fun onFilterBtnClicked() {
        viewState.navigateToAnimalFilter(animalFilter)
    }
}