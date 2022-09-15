package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.dto.AnimalCharacteristic

interface AnimalCharacteristicsRepository {
    suspend fun getAnimalColors(token: String): List<AnimalCharacteristic>
}