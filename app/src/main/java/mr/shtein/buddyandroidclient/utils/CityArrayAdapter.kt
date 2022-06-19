package mr.shtein.buddyandroidclient.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem
import java.util.*
import kotlin.collections.ArrayList

class CityArrayAdapter(
    context: Context, private val cityList: MutableList<CityChoiceItem>
) : ArrayAdapter<CityChoiceItem>(
    context, R.layout.one_string_row, cityList
), Filterable {

    private var mCity = cityList
    private var mOriginalValues: ArrayList<CityChoiceItem>? = null

    override fun getCount() = mCity.size

    override fun getItem(position: Int) = mCity[position]

    override fun getPosition(item: CityChoiceItem?): Int {
        return mCity.indexOf(item)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                if (mOriginalValues == null) {
                    mOriginalValues = ArrayList(mCity)
                }

                if (constraint == null || constraint.isEmpty()) {
                    val list = ArrayList(mOriginalValues!!)
                    filterResults.values = list
                    filterResults.count = list.size
                } else {
                    val constraintString = constraint.toString().lowercase()
                    val values = ArrayList(mOriginalValues!!)
                    val newValues = arrayListOf<CityChoiceItem>()
                    for (city in values) {
                        val valueText = city.name.lowercase()
                        if (valueText.startsWith(constraintString)) {
                            newValues.add(city)
                        }
                    }
                    filterResults.values = newValues
                    filterResults.count = newValues.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                mCity = results.values as MutableList<CityChoiceItem>
                if (results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}