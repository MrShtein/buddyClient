package mr.shtein.buddyandroidclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.CitiesViewHolder
import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem

class CitiesAdapter(
    context: Context,
    var cityChoiceItems: List<CityChoiceItem>,
    var onCityListener: OnCityListener
) : RecyclerView.Adapter<CitiesViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesViewHolder {
        return CitiesViewHolder(inflater.inflate(R.layout.city_row, parent, false), onCityListener, this)
    }

    override fun onBindViewHolder(holderCities: CitiesViewHolder, position: Int) {
        holderCities.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return cityChoiceItems.size
    }

    private fun getItem(position: Int): CityChoiceItem = cityChoiceItems[position]


}