package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.dto.AnimalType

interface AnimalTypeRepository {
    suspend fun getAnimalTypes(): List<AnimalType>
}
