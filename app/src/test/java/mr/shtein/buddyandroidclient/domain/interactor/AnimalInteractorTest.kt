@file:OptIn(ExperimentalCoroutinesApi::class)
package mr.shtein.buddyandroidclient.domain.interactor

import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import mr.shtein.buddyandroidclient.data.repository.AnimalRepository
import mr.shtein.buddyandroidclient.data.repository.DistanceCounterRepository
import mr.shtein.buddyandroidclient.model.Animal
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub

class AnimalInteractorTest {

    private val animalRepository: AnimalRepository = mock<AnimalRepository>()
    private val networkDistanceCounterRepository: DistanceCounterRepository =
        mock<DistanceCounterRepository>()

    @Test
    fun `should return the same data as in repository`() = runBlocking {
        val faker: Faker = Faker()
        val testAnimalList: List<Animal> = List(50) {
            faker.randomProvider.randomClassInstance()
        }
        Mockito.`when`(animalRepository.getAnimals(listOf(1,2))).thenReturn(testAnimalList)

        val animalInteractor: AnimalInteractor = AnimalInteractorImpl(
            animalRepository, networkDistanceCounterRepository
        )
        val expectedAnimalList: List<Animal> = testAnimalList.map {
            it.copy()
        }
        val animalListFromInteractor = animalInteractor.getAnimalsByFilter(listOf(1,2))

        Assertions.assertEquals(expectedAnimalList[1], animalListFromInteractor[1])

    }

}