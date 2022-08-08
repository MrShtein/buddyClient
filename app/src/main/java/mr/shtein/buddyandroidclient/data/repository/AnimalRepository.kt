package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.Animal

interface AnimalRepository {
    suspend fun getAnimals(animalTypeList: List<Int>): List<Animal>
}