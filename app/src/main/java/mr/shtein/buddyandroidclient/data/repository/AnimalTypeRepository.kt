package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.model.AnimalType

interface AnimalTypeRepository {
    suspend fun getAnimalTypes(): List<AnimalType>
}
