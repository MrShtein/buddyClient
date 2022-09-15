package mr.shtein.buddyandroidclient.domain.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.data.repository.AnimalRepository
import mr.shtein.buddyandroidclient.data.repository.AnimalTypeRepository
import mr.shtein.buddyandroidclient.data.repository.DistanceCounterRepository
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.Coordinates
import mr.shtein.buddyandroidclient.model.dto.AnimalFilter
import mr.shtein.buddyandroidclient.model.dto.AnimalType
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.jvm.Throws

interface AnimalInteractor {
    @Throws(ConnectException::class, SocketTimeoutException::class, ServerErrorException::class)
    suspend fun getAnimalsByFilter(animalFilter: AnimalFilter): List<Animal>
    suspend fun getDistancesFromUser(token: String, coordinates: Coordinates): HashMap<Int, Int>
    suspend fun getAnimalTypes(): List<AnimalType>
}

class AnimalInteractorImpl(
    private val animalRepository: AnimalRepository,
    private val retrofitDistanceCounterRepository: DistanceCounterRepository,
    private val animalTypeRepository: AnimalTypeRepository
) : AnimalInteractor {
    override suspend fun getAnimalsByFilter(animalFilter: AnimalFilter): List<Animal> =
        withContext(Dispatchers.IO) {
            return@withContext animalRepository.getAnimals(animalFilter)
        }

    override suspend fun getDistancesFromUser(
        token: String,
        coordinates: Coordinates
    ): HashMap<Int, Int> {
        return retrofitDistanceCounterRepository.getDistancesFromUser(token, coordinates)
    }

    override suspend fun getAnimalTypes(): List<AnimalType> {
        return animalTypeRepository.getAnimalTypes()
    }

}