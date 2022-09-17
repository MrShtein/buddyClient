package mr.shtein.buddyandroidclient.domain.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.data.repository.*
import mr.shtein.buddyandroidclient.exceptions.validate.IncorrectDataException
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.Coordinates
import mr.shtein.buddyandroidclient.model.dto.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.jvm.Throws

private const val COLOR_ID = 1

interface AnimalInteractor {
    @Throws(ConnectException::class, SocketTimeoutException::class, ServerErrorException::class)
    suspend fun getAnimalsByFilter(animalFilter: AnimalFilter): List<Animal>
    suspend fun getDistancesFromUser(token: String, coordinates: Coordinates): HashMap<Int, Int>
    suspend fun getAnimalTypes(): List<AnimalType>
    suspend fun getAnimalBreeds(animalTypeId: Int): List<FilterAutocompleteItem>
    suspend fun getAnimalCharacteristics(characteristicId: Int): List<AnimalCharacteristic>
}

class AnimalInteractorImpl(
    private val animalRepository: AnimalRepository,
    private val animalBreedRepository: AnimalBreedRepository,
    private val sharedUserPropertiesRepository: UserPropertiesRepository,
    private val animalCharacteristicsRepository: AnimalCharacteristicsRepository,
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

    override suspend fun getAnimalBreeds(animalTypeId: Int): List<FilterAutocompleteItem> {
        val token = getUserToken()
        val animalBreeds =
            animalBreedRepository.getAnimalBreeds(token = token, animalTypeId = animalTypeId)
        return mapBreedsToFilterBreeds(breeds = animalBreeds)
    }

    override suspend fun getAnimalCharacteristics(characteristicId: Int): List<AnimalCharacteristic> {
        val token = getUserToken()
        return when (characteristicId) {
            COLOR_ID -> {
                animalCharacteristicsRepository.getAnimalColors(token)
            }
            else -> {
                throw IncorrectDataException()
            }
        }
    }

    private fun getUserToken(): String {
        return sharedUserPropertiesRepository.getUserToken()
    }

    private fun mapBreedsToFilterBreeds(breeds: List<Breed>): List<FilterAutocompleteItem> {
        return breeds
            .map {
                FilterAutocompleteItem(
                    it.id,
                    it.name,
                    false
                )
            }
            .toList()
    }
}