package mr.shtein.animal.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.IncorrectDataException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.Animal
import mr.shtein.data.model.AnimalFilter
import mr.shtein.data.model.Coordinates
import mr.shtein.data.repository.*
import mr.shtein.domain.interactor.AnimalInteractor
import mr.shtein.model.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.jvm.Throws

private const val COLOR_ID = 1

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

    override suspend fun getAnimalTypes(): List<FilterAutocompleteItem> {
        val animalType = animalTypeRepository.getAnimalTypes()
        return mapAnimalTypeToFilterItem(animalType)
    }

    override suspend fun getAnimalBreeds(animalTypeList: Set<Int>): List<FilterAutocompleteItem> {
        val allBreeds = mutableListOf<Breed>()
        val currentList = animalTypeList.toMutableList()
        if (currentList.isEmpty()) {
            currentList.add(DOG_ID)
            currentList.add(CAT_ID)
        }
        currentList.forEach {
            val animalBreeds =
                animalBreedRepository.getAnimalBreeds(animalTypeId = it)
            allBreeds.addAll(animalBreeds)
        }
        return mapBreedsToFilterBreeds(breeds = allBreeds)
    }

    override suspend fun getAnimalCharacteristics(characteristicId: Int): List<FilterAutocompleteItem> {
        return when (characteristicId) {
            COLOR_ID -> {
                val animalColors = animalCharacteristicsRepository.getAnimalColors()
                mapCharacteristicToFilterItem(animalColors)
            }
            else -> {
                throw IncorrectDataException()
            }
        }
    }

    override suspend fun getAnimalsCountByFilter(animalFilter: AnimalFilter): Int =
        withContext(Dispatchers.IO) {
            return@withContext animalRepository.getAnimalsCountByFilter(animalFilter)
        }

    override suspend fun getAnimalById(animalId: Long): Animal {
        return animalRepository.getAnimalById(animalId)
    }

    private fun mapAnimalTypeToFilterItem(
        animalType: List<AnimalType>
    ): List<FilterAutocompleteItem> {
        return animalType
            .map {
                FilterAutocompleteItem(id = it.id, name = it.pluralAnimalType, isSelected = false)
            }
            .toList()
    }

    private fun mapCharacteristicToFilterItem(
        animalColors: List<AnimalCharacteristic>
    ): List<FilterAutocompleteItem> {
        return animalColors
            .map {
                FilterAutocompleteItem(id = it.id, name = it.name, isSelected = false)
            }
            .toList()
    }

    private fun getUserToken(): String {
        return sharedUserPropertiesRepository.getUserToken()
    }

    private fun mapBreedsToFilterBreeds(breeds: List<Breed>): List<FilterAutocompleteItem> {
        return breeds
            .map {
                FilterAutocompleteItem(id = it.id, name = it.name, isSelected = false)
            }
            .toList()
    }

    companion object {
        private val DOG_ID = 1
        private val CAT_ID = 2
    }

}