package mr.shtein.buddyandroidclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.ViewHolder
import mr.shtein.buddyandroidclient.model.CityChoiceItem

class CitiesAdapter(
    context: Context,
    var cityChoiceItems: List<CityChoiceItem>,
    var onCityListener: OnCityListener
) : RecyclerView.Adapter<ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var cityChoiceItem: CityChoiceItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.city_row, parent, false), onCityListener, this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return cityChoiceItems.size
    }

    fun getItem(position: Int): CityChoiceItem = cityChoiceItems[position]


}