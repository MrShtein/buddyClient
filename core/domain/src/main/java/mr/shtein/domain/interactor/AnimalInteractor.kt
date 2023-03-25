package mr.shtein.domain.interactor

import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.Animal
import mr.shtein.data.model.AnimalFilter
import mr.shtein.data.model.Coordinates
import mr.shtein.model.FilterAutocompleteItem
import java.net.ConnectException
import java.net.SocketTimeoutException

interface AnimalInteractor {
    @Throws(ConnectException::class, SocketTimeoutException::class, ServerErrorException::class)
    suspend fun getAnimalsByFilter(animalFilter: AnimalFilter): List<Animal>
    suspend fun getDistancesFromUser(token: String, coordinates: Coordinates): HashMap<Int, Int>
    suspend fun getAnimalTypes(): List<FilterAutocompleteItem>
    suspend fun getAnimalBreeds(animalTypeList: Set<Int>): List<FilterAutocompleteItem>
    suspend fun getAnimalCharacteristics(characteristicId: Int): List<FilterAutocompleteItem>
    suspend fun getAnimalsCountByFilter(animalFilter: AnimalFilter): Int

    suspend fun getAnimalById(animalId: Long) : Animal
}