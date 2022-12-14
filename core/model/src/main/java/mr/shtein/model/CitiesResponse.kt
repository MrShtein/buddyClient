package mr.shtein.model

import mr.shtein.model.CityChoiceItem

data class CitiesResponse(
    val citiesList: List<CityChoiceItem>,
    val error: String
)
