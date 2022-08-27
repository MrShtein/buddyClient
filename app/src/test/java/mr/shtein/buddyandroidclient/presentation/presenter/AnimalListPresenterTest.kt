@file:OptIn(ExperimentalCoroutinesApi::class)

package mr.shtein.buddyandroidclient.presentation.presenter

import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractor
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractorImpl
import mr.shtein.buddyandroidclient.domain.interactor.LocationInteractor
import mr.shtein.buddyandroidclient.domain.interactor.LocationServiceInteractor
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.Coordinates
import mr.shtein.buddyandroidclient.model.Kennel
import mr.shtein.buddyandroidclient.model.LocationState
import mr.shtein.buddyandroidclient.presentation.screen.AnimalListView
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.koin.java.KoinJavaComponent.get
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.internal.verification.VerificationModeFactory.times
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AnimalListPresenterTest {

    private lateinit var animalList: List<Animal>
    private lateinit var faker: Faker
    private lateinit var animalInteractor: AnimalInteractor
    private lateinit var animalListView: AnimalListView
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
                    Kennel(
                        faker.random.nextInt(min = 1, max = 2),
                        faker.funnyName.name(),
                        faker.address.fullAddress(),
                        faker.phoneNumber.phoneNumber(),
                        faker.internet.email(),
                        faker.internet.domain(),
                        Coordinates(55.5,55.6)
                    )
                }
            }
        }
        animalInteractor = mock<AnimalInteractor>()
        animalListView = mock<AnimalListView>()
        locationService = mock<LocationInteractor>()
        userPropertiesRepository = mock<UserPropertiesRepository>()
        testDispatcher = UnconfinedTestDispatcher()
        animalListPresenter = AnimalsListPresenterImpl(
            animalInteractor, locationService, userPropertiesRepository, testDispatcher
        )
    }

    @AfterEach
    fun resetMocks() {
        Mockito.reset(animalInteractor, animalListView, locationService, userPropertiesRepository)
    }

    @Test
    fun `should go to the first if condition and update animal list`() {
        val isDogChecked = true
        val isCatChecked = true
        val getFromNetwork = false
        animalListPresenter.onAttachView(animalListView)


        animalListPresenter.onAnimalShowCommand(isDogChecked, isCatChecked, getFromNetwork)
        verify(animalListView, times(1)).showAnimalSearchProgressBar()
        verify(animalListView, times(1)).updateList(animalList)
        verify(animalListView, times(1)).hideAnimalSearchProgressBar()
    }

    @Test
    fun `should go to the first if condition and make idUiMustUpdate to true`() {
        val isDogChecked = true
        val isCatChecked = true
        val getFromNetwork = false
        animalListPresenter.animalList = this.animalList

        animalListPresenter.onAnimalShowCommand(isDogChecked, isCatChecked, getFromNetwork)

        Assertions.assertEquals(animalListPresenter.isUiMustUpdate, true)
    }

    @Test
    fun `should update UI and update IsUiMustUpdate if isUiMustUpdate == true`() {
        val isDogChecked = true
        val isCatChecked = true
        val getFromNetwork = true
        animalListPresenter.animalList = this.animalList
        animalListPresenter.isUiMustUpdate = true

        animalListPresenter.onAttachView(animalListView)
        animalListPresenter.onAnimalShowCommand(isDogChecked, isCatChecked, getFromNetwork)
        verify(animalListView, times(1))
            .updateList(animalListPresenter.animalList!!)
        Assertions.assertEquals(animalListPresenter.isUiMustUpdate, false)
        verify(animalListView, times(1))
            .updateList(animalListPresenter.animalList!!)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should show animalList without location`() = runTest(testDispatcher.scheduler) {
        val isDogChecked = true
        val isCatChecked = true
        val getFromNetwork = true

        animalListPresenter.onAttachView(animalListView)
        Mockito.`when`(animalListView.checkLocationPermission()).thenReturn(false)
        Mockito.`when`(animalInteractor.getAnimalsByFilter(listOf(1, 2))).thenReturn(animalList)
        animalListPresenter.onAnimalShowCommand(isDogChecked, isCatChecked, getFromNetwork)

        advanceUntilIdle()
        Assertions.assertNotNull(animalListPresenter.animalList)
        verify(animalListView, times(1)).updateList(animalListPresenter.animalList!!)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should show animalList with location`() = runTest(testDispatcher.scheduler) {
        val isDogChecked = true
        val isCatChecked = true
        val getFromNetwork = true
        val token = "123"
        val coordinates = Coordinates(55.3,53.3)

        animalListPresenter.onAttachView(animalListView)

        Mockito.`when`(animalInteractor.getAnimalsByFilter(listOf(1, 2))).thenReturn(animalList)
        Mockito.`when`(animalListView.checkLocationPermission()).thenReturn(true)
        Mockito.`when`(userPropertiesRepository.getUserToken()).thenReturn(token)
        Mockito.`when`(locationService.getCurrentDistance()).thenReturn(coordinates)
        Mockito.`when`(animalInteractor.getDistancesFromUser(token, coordinates)).thenReturn(hashMapOf(1 to 5500, 2 to 300))
        animalList.forEach {
            if (it.kennel.id == 1) {
                it.distance = "5.5 км. от Вас"
            } else {
                it.distance = "300 м. от Вас"
            }
            it.locationState = LocationState.DISTANCE_VISIBLE_STATE
        }

        animalListPresenter.onAnimalShowCommand(isDogChecked, isCatChecked, getFromNetwork)
        advanceUntilIdle()

        Assertions.assertIterableEquals(animalList, animalListPresenter.animalList)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should show animalList with location when user click to locationBtn`() = runTest(testDispatcher.scheduler) {
        val token = "123"
        val coordinates = Coordinates(55.3,53.3)
        Mockito.`when`(animalInteractor.getAnimalsByFilter(listOf(1, 2))).thenReturn(animalList)
        Mockito.`when`(userPropertiesRepository.getUserToken()).thenReturn(token)
        Mockito.`when`(locationService.getCurrentDistance()).thenReturn(coordinates)
        Mockito.`when`(animalInteractor.getDistancesFromUser(token, coordinates)).thenReturn(hashMapOf(1 to 5500, 2 to 300))
        animalListPresenter.animalList = animalList
        val permissions = mapOf("Permission" to true)

        animalListPresenter.onAttachView(animalListView)

        animalList.forEach {
            if (it.kennel.id == 1) {
                it.distance = "5.5 км. от Вас"
            } else {
                it.distance = "300 м. от Вас"
            }
            it.locationState = LocationState.DISTANCE_VISIBLE_STATE
        }

        animalListPresenter.onClickToLocationBtn(permissions)
        verify(animalListView, times(1)).updateList(animalListPresenter.animalList!!) //TODO Узнать, что может быть не так (updateList вызывается 2 раза, но тест не проходит)
        advanceUntilIdle()
        verify(animalListView, times(1)).updateList(animalListPresenter.animalList!!) //TODO Узнать, что может быть не так (updateList вызывается 2 раза, но тест не проходит)
        Assertions.assertIterableEquals(animalList, animalListPresenter.animalList)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should show errorMessage without location when user click to locationBtn`() = runTest(testDispatcher.scheduler) {
        val permissions = mapOf("Permission" to false)
        animalListPresenter.onAttachView(animalListView)


        animalListPresenter.onClickToLocationBtn(permissions)
        advanceUntilIdle()
        verify(animalListView, times(0)).updateList(animalList)
        verify(animalListView, times(1)).showLocationFailureText(anyInt())
    }
}

