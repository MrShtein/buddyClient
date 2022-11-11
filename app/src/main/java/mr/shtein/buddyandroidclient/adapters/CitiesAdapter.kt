package mr.shtein.buddyandroidclient.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.R
import mr.shtein.model.CityChoiceItem

class CitiesAdapter(
    var cityChoiceItems: List<CityChoiceItem>,
    var onCityListener: OnCityListener
) : RecyclerView.Adapter<CitiesAdapter.CitiesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return CitiesViewHolder(inflater.inflate(R.layout.city_row, parent, false), onCityListener)
    }

    override fun onBindViewHolder(holderCities: CitiesViewHolder, position: Int) {
        val cityCard = getItem(position)
        holderCities.bind(cityCard)
    }

    override fun getItemCount(): Int {
        return cityChoiceItems.size
    }

    private fun getItem(position: Int): CityChoiceItem = cityChoiceItems[position]

    inner class CitiesViewHolder(itemView: View, var onCityListener: OnCityListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

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
            onCityListener.onCityClick(cityChoiceItems[absoluteAdapterPosition])
        }
    }


}