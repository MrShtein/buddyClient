package mr.shtein.buddyandroidclient.model.response

import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem

data class CitiesResponse(
    val citiesList: List<CityChoiceItem>,
    val error: String
)
