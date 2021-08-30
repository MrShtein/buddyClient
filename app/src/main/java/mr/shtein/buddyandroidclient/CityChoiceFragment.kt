package mr.shtein.buddyandroidclient

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.adapters.CitiesAdapter
import mr.shtein.buddyandroidclient.model.City
import mr.shtein.buddyandroidclient.utils.CityCallback

class CityChoiceFragment : Fragment() {

    private lateinit var  cities: List<City>
    private var lettersCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.city_choice_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cities = getCitiesFromFile("cities.txt", this.requireContext())

        val adapter = this.context?.let { CitiesAdapter(it, cities) }

        val list = view.findViewById<RecyclerView>(R.id.city_list)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

        fun onCitiesChanged(letters: String) {
            var newCityList: List<City>? = null
            when {
                letters.isEmpty() -> {
                    newCityList = cities
                    lettersCount = 0
                }
                letters.length < lettersCount -> {
                    lettersCount--
                    newCityList = cities.filter {
                        it.cityName.lowercase().startsWith(letters.lowercase())
                    }
                }
                else -> {
                    lettersCount++
                    newCityList = adapter!!.cities.filter {
                        it.cityName.lowercase().startsWith(letters.lowercase())
                    }
                }
            }

            val callback = CityCallback(adapter!!.cities, newCityList)
            val diff = DiffUtil.calculateDiff(callback)
            diff.dispatchUpdatesTo(adapter)
            adapter.cities = newCityList
        }

        val cityInput = view.findViewById<EditText>(R.id.city_choice_edit_text)

        cityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    onCitiesChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                return
            }
        })



    }



    private fun getCities(): List<City> {
        return listOf(
            City("Москва"),
            City("Санкт-Петербург"),
            City("Новосибирск"),
            City("Екатеринбург"),
            City("Казань"),
            City("Нижний Новгород"),
            City("Челябинск"),
            City("Самара"),
            City("Омск"),
            City("Ростов-на-Дону"),
            City("Уфа"),
            City("Красноярск"),
            City("Воронеж"),
            City("Волгоград"),
            City("Краснодар"),
            City("Саратов"),
            City("Тюмень"),
            City("Тольятти"),
            City("Ижевск"),
            City("Барнаул"),
            City("Ульяновск"),
            City("Иркутск"),

            )
    }

    private fun getCitiesFromFile(address: String, context: Context): List<City> {
        val cityList = mutableListOf<City>()
        val str =  context.assets.open(address).readBytes().toString(Charsets.UTF_8).split("\n").toList()
        str.forEach {
            cityList.add(City(it))
        }
        return cityList
    }
}