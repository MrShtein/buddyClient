package mr.shtein.city.data

import mr.shtein.model.CityChoiceItem

interface CityRepository {
    suspend fun getCities(): MutableList<CityChoiceItem>
}