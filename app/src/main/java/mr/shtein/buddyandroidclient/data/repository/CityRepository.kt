package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.model.CityChoiceItem

interface CityRepository {
    suspend fun getCities(): MutableList<CityChoiceItem>
}