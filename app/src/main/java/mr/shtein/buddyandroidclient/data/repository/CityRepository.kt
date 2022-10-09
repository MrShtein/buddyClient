package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem

interface CityRepository {
    suspend fun getCities(): MutableList<CityChoiceItem>
}