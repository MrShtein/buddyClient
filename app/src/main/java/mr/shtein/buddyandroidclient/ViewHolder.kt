package mr.shtein.buddyandroidclient

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.model.City

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val city: TextView = itemView.findViewById(R.id.city_name)

    fun bind(city: City) {
        this.city.text = city.cityName
    }
}