package mr.shtein.buddyandroidclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.ViewHolder
import mr.shtein.buddyandroidclient.model.City

class CitiesAdapter(
    context: Context,
    var cities: List<City>
) : RecyclerView.Adapter<ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var city: City

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.city_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = getItem(position)

        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    fun getItem(position: Int): City = cities[position]


}