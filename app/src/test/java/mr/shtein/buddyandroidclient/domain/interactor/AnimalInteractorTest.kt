@file:OptIn(ExperimentalCoroutinesApi::class)
package mr.shtein.buddyandroidclient.domain.interactor

import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import mr.shtein.animal.domain.AnimalInteractor
import mr.shtein.animal.domain.AnimalInteractorImpl
import mr.shtein.buddyandroidclient.data.repository.AnimalRepository
import mr.shtein.buddyandroidclient.data.repository.DistanceCounterRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock

class AnimalInteractorTest {

    private val animalRepository: AnimalRepository = mock<AnimalRepository>()
    private val networkDistanceCounterRepository: DistanceCounterRepository =
        mock<DistanceCounterRepository>()

    @Test
    fun `should return the same data as in repository`() = runBlocking {
        val faker: Faker = Faker()
        val testAnimalList: List<mr.shtein.data.model.Animal> = List(50) {
            faker.randomProvider.randomClassInstance()
        }
        Mockito.`when`(animalRepository.getAnimals(listOf(1,2))).thenReturn(testAnimalList)

        val animalInteractor: AnimalInteractor = AnimalInteractorImpl(
            animalRepository, networkDistanceCounterRepository
        )
        val expectedAnimalList: List<mr.shtein.data.model.Animal> = testAnimalList.map {
            it.copy()
        }
        val animalListFromInteractor = animalInteractor.getAnimalsByFilter(listOf(1,2))

        Assertions.assertEquals(expectedAnimalList[1], animalListFromInteractor[1])

    }

}