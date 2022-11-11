package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.model.AnimalCharacteristic

interface AnimalCharacteristicsRepository {
    suspend fun getAnimalColors(): List<AnimalCharacteristic>
}