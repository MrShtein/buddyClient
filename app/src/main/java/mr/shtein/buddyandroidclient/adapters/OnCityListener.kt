package mr.shtein.buddyandroidclient.adapters

interface OnCityListener {
    fun onCityClick(position: Int, adapter: CitiesAdapter): Unit
}