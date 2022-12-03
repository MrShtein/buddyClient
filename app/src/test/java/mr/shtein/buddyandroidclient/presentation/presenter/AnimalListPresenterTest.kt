@file:OptIn(ExperimentalCoroutinesApi::class)

package mr.shtein.buddyandroidclient.presentation.presenter

import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractor
import mr.shtein.buddyandroidclient.domain.interactor.LocationInteractor
import mr.shtein.data.exception.LocationServiceException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.buddyandroidclient.presentation.screen.`AnimalListView$$State`
import mr.shtein.ui_util.FragmentsListForAssigningAnimation
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.ArgumentMatchers.*
import org.mockito.InOrder
import org.mockito.Mockito
import org.mockito.kotlin.*
import java.lang.RuntimeException
import java.net.ConnectException
import java.net.SocketTimeoutException

private const val PERMISSION_DENIED = -1
private const val PERMISSION_GRANTED = 0
private const val LATITUDE = 5.5
private const val LONGITUDE = 6.5
private const val TOKEN = "test_token_string"
private val FILTER_LIST = listOf(1, 2)
private val DISTANCES = hashMapOf<Int, Int>(1 to 1)
private val COORDINATES = mr.shtein.data.model.Coordinates(LATITUDE, LONGITUDE)
private const val IS_DOG_CHECKED = true
private const val IS_CAT_CHECKED = true
private const val IS_FROM_NETWORK_POSITIVE = true
private const val IS_FROM_NETWORK_NEGATIVE = false

private const val ANIMAL_CARD_LABEL = "AnimalsCardFragment"
private const val KENNEL_LABEL = "AddKennelFragment"
private const val USER_PROFILE_LABEL = "UserProfileFragment"
private const val REGISTRATION_LABEL = "UserRegistrationFragment"
private const val LOGIN_LABEL = "LoginFragment"


@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnimalListPresenterTest {

    private lateinit var animalList: List<mr.shtein.data.model.Animal>
    private lateinit var faker: Faker
    private lateinit var animalInteractor: AnimalInteractor
    private lateinit var animalListView: `AnimalListView$$State`
    private lateinit var locationService: LocationInteractor
    private lateinit var userPropertiesRepository: UserPropertiesRepository
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var animalListPresenter: AnimalsListPresenterImpl

    @BeforeAll
    fun initValues() {
        faker = Faker()
        animalList = List(50) {
            faker.randomProvider.randomClassInstance() {
                typeGenerator {
                    mr.shtein.data.model.Kennel(
                        faker.random.nextInt(min = 1, max = 2),
                        faker.funnyName.name(),
                        faker.address.fullAddress(),
                        faker.phoneNumber.phoneNumber(),
                        faker.internet.email(),
                        faker.internet.domain(),
                        COORDINATES
                    )
                }
            }
        }
        animalInteractor = mock<AnimalInteractor>()
        animalListView = mock<`AnimalListView$$State`>()
        locationService = mock<LocationInteractor>()
        userPropertiesRepository = mock<UserPropertiesRepository>()
        testDispatcher = UnconfinedTestDispatcher()
        animalListPresenter = AnimalsListPresenterImpl(
            animalInteractor, locationService, userPropertiesRepository, testDispatcher
        )
        animalListPresenter.setViewState(animalListView)
    }

    @AfterEach
    fun resetMocks() {
        Mockito.reset(animalInteractor, animalListView, locationService, userPropertiesRepository)
    }

    @Test
    fun `should show animals from memory`() = runTest(testDispatcher.scheduler) {
        val inOrder: InOrder = Mockito.inOrder(animalListView)
        Mockito.`when`(animalInteractor.getAnimalsByFilter(anyList<Int>())).thenReturn(animalList)
        animalListPresenter.onInit(
            fineLocationPermission = PERMISSION_DENIED,
            coarseLocationPermission = PERMISSION_DENIED
        )


        animalListPresenter.onAnimalShowCommand(IS_DOG_CHECKED, IS_CAT_CHECKED, IS_FROM_NETWORK_POSITIVE)
        inOrder.verify(animalListView).toggleAnimalSearchProgressBar(isVisible = true)
        advanceUntilIdle()
        inOrder.verify(animalListView).updateList(anyList<mr.shtein.data.model.Animal>())
        inOrder.verify(animalListView).toggleAnimalSearchProgressBar(isVisible = false)

        animalListPresenter.onAnimalShowCommand(IS_DOG_CHECKED, IS_CAT_CHECKED, IS_FROM_NETWORK_NEGATIVE)
        inOrder.verify(animalListView).toggleAnimalSearchProgressBar(isVisible = true)
        inOrder.verify(animalListView).updateList(anyList<mr.shtein.data.model.Animal>())
        inOrder.verify(animalListView).toggleAnimalSearchProgressBar(isVisible = false)
    }

    @Test
    fun `should show animals without location data from network`() = runTest(testDispatcher.scheduler) {
        val isDogChecked = true
        val isCatChecked = true
        val getFromNetwork = true
        val inOrder: InOrder = Mockito.inOrder(animalListView, animalInteractor)
        Mockito.`when`(animalInteractor.getAnimalsByFilter(anyList<Int>())).thenReturn(animalList)
        animalListPresenter.onInit(
            fineLocationPermission = PERMISSION_DENIED,
            coarseLocationPermission = PERMISSION_DENIED
        )

        animalListPresenter.onAnimalShowCommand(isDogChecked, isCatChecked, getFromNetwork)
        advanceUntilIdle()

        inOrder.verify(animalListView).toggleAnimalSearchProgressBar(isVisible = true)
        inOrder.verify(animalInteractor).getAnimalsByFilter(anyList())
        inOrder.verify(animalListView).updateList(anyList())
        inOrder.verify(animalListView).toggleAnimalSearchProgressBar(isVisible = false)
    }

    @ParameterizedTest
    @CsvSource(
        value =
        [
            "$PERMISSION_DENIED, $PERMISSION_GRANTED",
            "$PERMISSION_GRANTED, $PERMISSION_DENIED"
        ]
    )
    fun `should show animals with location data from network`(
        fineLocationPermission: Int,
        coarseLocationPermission: Int
    ) = runTest(testDispatcher.scheduler) {
        val inOrder: InOrder =
            Mockito.inOrder(animalListView, animalInteractor, userPropertiesRepository, locationService)
        Mockito.`when`(animalInteractor.getAnimalsByFilter(FILTER_LIST)).thenReturn(animalList)
        Mockito.`when`(userPropertiesRepository.getUserToken()).thenReturn(TOKEN)
        Mockito.`when`(locationService.getCurrentDistance()).thenReturn(COORDINATES)
        Mockito.`when`(animalInteractor.getDistancesFromUser(TOKEN, COORDINATES)).thenReturn(DISTANCES)
        animalListPresenter.onInit(
            fineLocationPermission,
            coarseLocationPermission
        )

        animalListPresenter.onAnimalShowCommand(IS_DOG_CHECKED, IS_CAT_CHECKED, IS_FROM_NETWORK_POSITIVE)

        inOrder.verify(animalListView).toggleAnimalSearchProgressBar(isVisible = true)
        inOrder.verify(animalInteractor).getAnimalsByFilter(FILTER_LIST)
        inOrder.verify(animalListView).updateList(anyList())
        inOrder.verify(userPropertiesRepository).getUserToken()
        inOrder.verify(locationService).getCurrentDistance()
        inOrder.verify(animalInteractor).getDistancesFromUser(TOKEN, COORDINATES)
        inOrder.verify(animalListView).updateList(anyList())
        inOrder.verify(animalListView).toggleAnimalSearchProgressBar(isVisible = false)
    }

    @Test
    fun `should show error toast when Connect Exception throws`() = runTest(testDispatcher.scheduler) {
        animalListPresenter.onInit(PERMISSION_DENIED, PERMISSION_DENIED)
        Mockito.`when`(animalInteractor.getAnimalsByFilter(FILTER_LIST)).thenThrow(ConnectException())

        animalListPresenter.onAnimalShowCommand(IS_DOG_CHECKED, IS_CAT_CHECKED, IS_FROM_NETWORK_POSITIVE)
        advanceUntilIdle()

        verify(animalListView, times(1)).showError(anyInt())
    }

    @Test
    fun `should show error toast when Socket Exception throws`() = runTest(testDispatcher.scheduler) {
        animalListPresenter.onInit(PERMISSION_DENIED, PERMISSION_DENIED)
        Mockito.`when`(animalInteractor.getAnimalsByFilter(FILTER_LIST)).thenThrow(SocketTimeoutException())

        animalListPresenter.onAnimalShowCommand(IS_DOG_CHECKED, IS_CAT_CHECKED, IS_FROM_NETWORK_POSITIVE)
        advanceUntilIdle()

        verify(animalListView, times(1)).showError(anyInt())
    }

    @Test
    fun `should show error toast when ServerError Exception throws`() = runTest(testDispatcher.scheduler) {
        animalListPresenter.onInit(PERMISSION_DENIED, PERMISSION_DENIED)
        Mockito.`when`(animalInteractor.getAnimalsByFilter(FILTER_LIST)).thenThrow(
            ServerErrorException()
        )

        animalListPresenter.onAnimalShowCommand(IS_DOG_CHECKED, IS_CAT_CHECKED, IS_FROM_NETWORK_POSITIVE)
        advanceUntilIdle()

        verify(animalListView, times(1)).showError(anyInt())
    }

    @Test
    fun `should show error toast when LocationService Exception throws`() = runTest(testDispatcher.scheduler) {
        Mockito.`when`(animalInteractor.getAnimalsByFilter(FILTER_LIST)).thenReturn(animalList)
        Mockito.`when`(userPropertiesRepository.getUserToken()).thenReturn(TOKEN)
        Mockito.`when`(locationService.getCurrentDistance()).thenThrow(LocationServiceException())
        Mockito.`when`(animalInteractor.getDistancesFromUser(TOKEN, COORDINATES)).thenReturn(DISTANCES)
        animalListPresenter.onInit(
            PERMISSION_GRANTED,
            PERMISSION_GRANTED
        )

        animalListPresenter.onAnimalShowCommand(IS_DOG_CHECKED, IS_CAT_CHECKED, IS_FROM_NETWORK_POSITIVE)
        advanceUntilIdle()

        verify(animalListView, times(2)).showError(anyInt())
    }

    @Test
    fun `should show animalList with location when user click to locationBtn and location permissions allowed`() =
        runTest(testDispatcher.scheduler) {
            Mockito.`when`(animalInteractor.getAnimalsByFilter(FILTER_LIST)).thenReturn(animalList)
            Mockito.`when`(locationService.getCurrentDistance()).thenReturn(COORDINATES)
            Mockito.`when`(userPropertiesRepository.getUserToken()).thenReturn(TOKEN)
            Mockito.`when`(animalInteractor.getDistancesFromUser(TOKEN, COORDINATES)).thenReturn(DISTANCES)
            val locationPermissions = hashMapOf<String, Boolean>(
                "FINE" to true,
                "COARSE" to false
            )
            val inOrder: InOrder =
                Mockito.inOrder(animalInteractor, animalListView, locationService, userPropertiesRepository)
            animalListPresenter.onInit(PERMISSION_DENIED, PERMISSION_DENIED)
            animalListPresenter.onAnimalShowCommand(IS_DOG_CHECKED, IS_CAT_CHECKED, IS_FROM_NETWORK_POSITIVE)

            animalListPresenter.onClickToLocationBtn(locationPermissions)
            advanceUntilIdle()

            inOrder.verify(animalListView).updateList(anyList())
            inOrder.verify(userPropertiesRepository).getUserToken()
            inOrder.verify(animalInteractor).getDistancesFromUser(TOKEN, COORDINATES)
            inOrder.verify(animalListView).updateList(anyList())

        }

    @Test
    fun `should show error message when user click to locationBtn and exception appear`() =
        runTest(testDispatcher.scheduler) {
            Mockito.`when`(animalInteractor.getAnimalsByFilter(FILTER_LIST)).thenReturn(animalList)
            Mockito.`when`(locationService.getCurrentDistance()).thenThrow(RuntimeException())
            val locationPermissions = hashMapOf<String, Boolean>(
                "FINE" to true,
                "COARSE" to false
            )
            val inOrder: InOrder = Mockito.inOrder(animalInteractor, animalListView, locationService)
            animalListPresenter.onInit(PERMISSION_DENIED, PERMISSION_DENIED)
            animalListPresenter.onAnimalShowCommand(IS_DOG_CHECKED, IS_CAT_CHECKED, IS_FROM_NETWORK_POSITIVE)

            animalListPresenter.onClickToLocationBtn(locationPermissions)
            advanceUntilIdle()

            inOrder.verify(animalListView).updateList(anyList())
            inOrder.verify(animalListView).showError(anyInt())
            inOrder.verify(animalListView).updateList(anyList())

        }

    @Test
    fun `should show error message when user click to locationBtn and location permissions denied`() =
        runTest(testDispatcher.scheduler) {
            Mockito.`when`(animalInteractor.getAnimalsByFilter(FILTER_LIST)).thenReturn(animalList)
            val locationPermissions = hashMapOf<String, Boolean>(
                "FINE" to false,
                "COARSE" to false
            )
            val inOrder: InOrder = Mockito.inOrder(animalListView)
            animalListPresenter.onInit(PERMISSION_DENIED, PERMISSION_DENIED)
            animalListPresenter.onAnimalShowCommand(IS_DOG_CHECKED, IS_CAT_CHECKED, IS_FROM_NETWORK_POSITIVE)

            animalListPresenter.onClickToLocationBtn(locationPermissions)
            advanceUntilIdle()

            inOrder.verify(animalListView).updateList(anyList())
            inOrder.verify(animalListView).showLocationFailureText(anyInt())

        }

    @Test
    fun `should change animal count text`() {
        val previousListSize = 12
        animalListPresenter.onUpdatedList(animalList, previousListSize)

        verify(animalListView, times(1)).showAnimalCountText(anyInt())
    }

    @Test
    fun `shouldn't change animal count text`() {
        val previousListSize = 50
        animalListPresenter.onUpdatedList(animalList, previousListSize)

        verifyNoInteractions(animalListView)
    }

    @Test
    fun `should set animation when go to animal_card`() {
        val nextFragment = FragmentsListForAssigningAnimation.ANIMAL_CARD
        animalListPresenter.onChangeAnimationsWhenNavigate(nextFragment)

        verify(animalListView, times(1)).setAnimationWhenToAnimalCardNavigate()
    }

    @Test
    fun `should set animation when go to add_kennel`() {
        val nextFragment = FragmentsListForAssigningAnimation.ADD_KENNEL
        animalListPresenter.onChangeAnimationsWhenNavigate(nextFragment)

        verify(animalListView, times(1)).setAnimationWhenToAddKennelNavigate()
    }

    @Test
    fun `should set animation when go to user_profile`() {
        val nextFragment = FragmentsListForAssigningAnimation.USER_PROFILE
        animalListPresenter.onChangeAnimationsWhenNavigate(nextFragment)

        verify(animalListView, times(1)).setAnimationWhenToUserProfileNavigate()
    }

    @Test
    fun `should set animation when go to other fragment`() {
        val nextFragment = FragmentsListForAssigningAnimation.OTHER
        animalListPresenter.onChangeAnimationsWhenNavigate(nextFragment)

        verify(animalListView, times(1)).setAnimationWhenToOtherFragmentNavigate()
    }

    @Test
    fun `should set animation when previous fragment is add_kennel`() {
        val previousFragment = FragmentsListForAssigningAnimation.ADD_KENNEL
        animalListPresenter.onChangeAnimationsWhenStartFragment(previousFragment)

        verify(animalListView, times(1)).setAnimationWhenUserComeFromAddKennel()
    }

    @Test
    fun `should set animation when previous fragment is user_profile`() {
        val previousFragment = FragmentsListForAssigningAnimation.USER_PROFILE
        animalListPresenter.onChangeAnimationsWhenStartFragment(previousFragment)

        verify(animalListView, times(1)).setAnimationWhenUserComeFromUserProfile()
    }

    @Test
    fun `should set animation when previous fragment is login`() {
        val previousFragment = FragmentsListForAssigningAnimation.LOGIN
        animalListPresenter.onChangeAnimationsWhenStartFragment(previousFragment)

        verify(animalListView, times(1)).setAnimationWhenUserComeFromLogin()
    }

    @Test
    fun `should set animation when previous fragment is city_choice`() {
        val previousFragment = FragmentsListForAssigningAnimation.CITY_CHOICE
        animalListPresenter.onChangeAnimationsWhenStartFragment(previousFragment)

        verify(animalListView, times(1)).setAnimationWhenUserComeFromCity()
    }

    @Test
    fun `should set animation when previous fragment is start`() {
        val previousFragment = FragmentsListForAssigningAnimation.START
        animalListPresenter.onChangeAnimationsWhenStartFragment(previousFragment)

        verify(animalListView, times(1)).setAnimationWhenUserComeFromSplash()
    }

    @Test
    fun `should set animation when previous fragment is other`() {
        val previousFragment = FragmentsListForAssigningAnimation.OTHER
        animalListPresenter.onChangeAnimationsWhenStartFragment(previousFragment)

        verifyNoMoreInteractions(animalListView)
    }

    @Test
    fun `should return animal_card`() {
        val listForAssigningAnimation = animalListPresenter.onGetListForAssigningAnimation(ANIMAL_CARD_LABEL)
        Assertions.assertEquals(listForAssigningAnimation, FragmentsListForAssigningAnimation.ANIMAL_CARD)
    }

    @Test
    fun `should return kennel`() {
        val listForAssigningAnimation = animalListPresenter.onGetListForAssigningAnimation(KENNEL_LABEL)
        Assertions.assertEquals(listForAssigningAnimation, FragmentsListForAssigningAnimation.ADD_KENNEL)
    }

    @Test
    fun `should return user_profile`() {
        val listForAssigningAnimation = animalListPresenter.onGetListForAssigningAnimation(USER_PROFILE_LABEL)
        Assertions.assertEquals(listForAssigningAnimation, FragmentsListForAssigningAnimation.USER_PROFILE)
    }

    @Test
    fun `should return registration`() {
        val listForAssigningAnimation = animalListPresenter.onGetListForAssigningAnimation(REGISTRATION_LABEL)
        Assertions.assertEquals(listForAssigningAnimation, FragmentsListForAssigningAnimation.REGISTRATION)
    }

    @Test
    fun `should return login`() {
        val listForAssigningAnimation = animalListPresenter.onGetListForAssigningAnimation(LOGIN_LABEL)
        Assertions.assertEquals(listForAssigningAnimation, FragmentsListForAssigningAnimation.LOGIN)
    }

    @Test
    fun `should return other type`() {
        val listForAssigningAnimation = animalListPresenter.onGetListForAssigningAnimation("")
        Assertions.assertEquals(listForAssigningAnimation, FragmentsListForAssigningAnimation.OTHER)
    }


}

