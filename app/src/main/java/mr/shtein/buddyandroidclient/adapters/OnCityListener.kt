package mr.shtein.buddyandroidclient.adapters

import mr.shtein.model.CityChoiceItem

interface OnCityListener {
    fun onCityClick(cityItem: CityChoiceItem)
}