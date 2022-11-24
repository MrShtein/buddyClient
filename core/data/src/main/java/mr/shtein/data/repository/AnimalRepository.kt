package mr.shtein.data.repository

import mr.shtein.data.model.Animal
import mr.shtein.data.model.AnimalFilter
import mr.shtein.model.AddOrUpdateAnimal
import mr.shtein.model.AnimalDTO
import okhttp3.RequestBody

interface AnimalRepository {
    suspend fun getAnimals(filter: AnimalFilter): List<Animal>

    suspend fun getAnimalsCountByFilter(animalFilter: AnimalFilter): Int

    suspend fun getAnimalsByKennelIdAndAnimalType(
        token: String,
        kennelId: Int,
        animalType: String
    ): MutableList<AnimalDTO>

    suspend fun addNewAnimal(
        token: String,
        addOrUpdateAnimalRequest: AddOrUpdateAnimal
    ): Int

    suspend fun addPhotoToTmpDir(
        token: String,
        bytes: RequestBody
    ): String

    suspend fun updateAnimal(
        token: String,
        addOrUpdateAnimalRequest: AddOrUpdateAnimal
    ): Animal

    suspend fun deleteAnimal(
        token: String,
        animalId: Long
    )
}