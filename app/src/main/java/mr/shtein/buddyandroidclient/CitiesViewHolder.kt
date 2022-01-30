package mr.shtein.buddyandroidclient

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.adapters.CitiesAdapter
import mr.shtein.buddyandroidclient.adapters.OnCityListener
import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem

class CitiesViewHolder(view: View, var onCityListener: OnCityListener, private val adapter: CitiesAdapter) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private val city: TextView = itemView.findViewById(R.id.city_name)
    private val region: TextView = itemView.findViewById(R.id.region_name)
    init {
        itemView.setOnClickListener(this)
    }

    fun bind(cityChoiceItem: CityChoiceItem) {
        this.city.text = cityChoiceItem.name
        this.region.text = cityChoiceItem.region
    }

    override fun onClick(v: View) {
        onCityListener.onCityClick(absoluteAdapterPosition, adapter)
    }
}