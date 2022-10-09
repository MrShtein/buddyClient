package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.dto.AnimalFilter

interface AnimalRepository {
    suspend fun getAnimals(filter: AnimalFilter): List<Animal>
    suspend fun getAnimalsCountByFilter(animalFilter: AnimalFilter): Int
}