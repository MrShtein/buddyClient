package mr.shtein.buddyandroidclient

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
import mr.shtein.buddyandroidclient.model.City
import mr.shtein.buddyandroidclient.utils.CityCallback

class CityChoiceFragment : Fragment() {

    private var cities = getCities()
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
                        it.cityName.startsWith(letters)
                    }
                }
                else -> {
                    lettersCount++
                    newCityList = adapter!!.cities.filter {
                        it.cityName.startsWith(letters)
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
}