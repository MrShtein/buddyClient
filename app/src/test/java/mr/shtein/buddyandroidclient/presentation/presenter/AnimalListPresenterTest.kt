package mr.shtein.buddyandroidclient.presentation.presenter

import io.github.serpro69.kfaker.Faker
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractor
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractorImpl
import mr.shtein.buddyandroidclient.domain.interactor.LocationInteractor
import mr.shtein.buddyandroidclient.domain.interactor.LocationServiceInteractor
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.presentation.screen.AnimalListView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.koin.java.KoinJavaComponent.get
import org.mockito.Mockito
import org.mockito.internal.verification.VerificationModeFactory.times
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AnimalListPresenterTest {

    private val faker: Faker = Faker()

    private val animalTestListWithNull: List<Animal>? = null
    private val animalList: List<Animal> = List(50) {
        faker.randomProvider.randomClassInstance()
    }
    private val animalInteractor: AnimalInteractor = mock<AnimalInteractor>()
    private val animalListView: AnimalListView = mock<AnimalListView>()
    private val locationService: LocationInteractor = mock<LocationInteractor>()
    private val userPropertiesRepository: UserPropertiesRepository =
        mock<UserPropertiesRepository>()
    private val animalListPresenter: AnimalListPresenter = AnimalsListPresenterImpl(
        animalInteractor, locationService, userPropertiesRepository
    )



    @AfterEach
    fun resetMocks() {
        Mockito.reset(animalInteractor, animalListView, locationService, userPropertiesRepository)
    }

    @Test
    fun `should go to first if condition`() {
        val isDogChecked = true
        val isCatChecked = true
        val getFromNetwork = false
        animalListPresenter as AnimalsListPresenterImpl
        animalListPresenter.animalList = this.animalList
        animalListPresenter.onAttachView(animalListView)

        animalListPresenter.onAnimalShowCommand(isDogChecked, isCatChecked, getFromNetwork)
        verify(animalListView, times(1)).showAnimalSearchProgressBar()
        verify(animalListView, times(1)).hideAnimalSearchProgressBar()
    }







}