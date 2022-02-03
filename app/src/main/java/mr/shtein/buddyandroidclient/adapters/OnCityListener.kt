package mr.shtein.buddyandroidclient.adapters

import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem

interface OnCityListener {
    fun onCityClick(cityItem: CityChoiceItem)
}