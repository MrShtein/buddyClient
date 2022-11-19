package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.AnimalFilter
import mr.shtein.model.AnimalDTO
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Path

interface AnimalRepository {
    suspend fun getAnimals(filter: AnimalFilter): List<Animal>
    suspend fun getAnimalsCountByFilter(animalFilter: AnimalFilter): Int
    suspend fun getAnimalsByKennelIdAndAnimalType(
        token: String,
        kennelId: Int,
        animalType: String
    ): MutableList<AnimalDTO>
}