package mr.shtein.buddyandroidclient

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.adapters.CitiesAdapter
import mr.shtein.buddyandroidclient.adapters.OnCityListener
import mr.shtein.buddyandroidclient.model.City

class ViewHolder(var view: View, var onCityListener: OnCityListener, val adapter: CitiesAdapter) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private val city: TextView = itemView.findViewById(R.id.city_name)
    init {
        itemView.setOnClickListener(this)
    }

    fun bind(city: City) {
        this.city.text = city.cityName
    }

    override fun onClick(v: View) {
        onCityListener.onCityClick(absoluteAdapterPosition, adapter)
    }
}