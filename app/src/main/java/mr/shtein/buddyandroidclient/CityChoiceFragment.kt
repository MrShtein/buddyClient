package mr.shtein.buddyandroidclient

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.adapters.CitiesAdapter
import mr.shtein.buddyandroidclient.adapters.OnCityListener
import mr.shtein.buddyandroidclient.model.CityChoiceItem
import mr.shtein.buddyandroidclient.utils.CityCallback

const val MAG = "City"

class CityChoiceFragment : Fragment(), OnCityListener {

    private lateinit var cityChoiceItems: List<CityChoiceItem>
    private var lettersCount = 0
    private var isCitiesVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.city_choice_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cityChoiceItems = getCitiesFromFile("cities.txt", this.requireContext())

        val adapter = this.context?.let { CitiesAdapter(it, cityChoiceItems, this) }

        val list = view.findViewById<RecyclerView>(R.id.city_list)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

        fun onCitiesChanged(letters: String) {
            var newCityChoiceItemList: List<CityChoiceItem>? = null
            when {
                letters.isEmpty() -> {
                    newCityChoiceItemList = cityChoiceItems
                    lettersCount = 0
                }
                letters.length < lettersCount -> {
                    lettersCount--
                    newCityChoiceItemList = cityChoiceItems.filter {
                        it.cityName.lowercase().startsWith(letters.lowercase())
                    }
                }
                else -> {
                    lettersCount++
                    newCityChoiceItemList = adapter!!.cityChoiceItems.filter {
                        it.cityName.lowercase().startsWith(letters.lowercase())
                    }
                }
            }

            val callback = CityCallback(adapter!!.cityChoiceItems, newCityChoiceItemList)
            val diff = DiffUtil.calculateDiff(callback)
            diff.dispatchUpdatesTo(adapter)
            adapter.cityChoiceItems = newCityChoiceItemList
        }

        val cityInput = view.findViewById<EditText>(R.id.city_choice_edit_text)


        cityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isCitiesVisible) {
                    animateCitiesAlpha(list)
                    isCitiesVisible = true
                }
                onCitiesChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                return
            }
        })
    }

    override fun onCityClick(position: Int, adapter: CitiesAdapter) {
        val bundle = Bundle()
        bundle.putString("city", adapter.cityChoiceItems[position].cityName)
        findNavController().navigate(R.id.animal_choice_fragment, bundle)
        Log.d(MAG, "onCityClick: ${adapter.cityChoiceItems[position].cityName}")
    }

    private fun animateCitiesAlpha(list: RecyclerView) {
        ObjectAnimator
            .ofFloat(list, "alpha", 1f).apply {
                duration = 500
            }
            .start()
    }


    private fun getCities(): List<CityChoiceItem> {
        return listOf(
            CityChoiceItem("Москва"),
            CityChoiceItem("Санкт-Петербург"),
            CityChoiceItem("Новосибирск"),
            CityChoiceItem("Екатеринбург"),
            CityChoiceItem("Казань"),
            CityChoiceItem("Нижний Новгород"),
            CityChoiceItem("Челябинск"),
            CityChoiceItem("Самара"),
            CityChoiceItem("Омск"),
            CityChoiceItem("Ростов-на-Дону"),
            CityChoiceItem("Уфа"),
            CityChoiceItem("Красноярск"),
            CityChoiceItem("Воронеж"),
            CityChoiceItem("Волгоград"),
            CityChoiceItem("Краснодар"),
            CityChoiceItem("Саратов"),
            CityChoiceItem("Тюмень"),
            CityChoiceItem("Тольятти"),
            CityChoiceItem("Ижевск"),
            CityChoiceItem("Барнаул"),
            CityChoiceItem("Ульяновск"),
            CityChoiceItem("Иркутск"),

            )
    }

    private fun getCitiesFromFile(address: String, context: Context): List<CityChoiceItem> {
        val cityList = mutableListOf<CityChoiceItem>()
        context.assets
            .open(address)
            .readBytes()
            .toString(Charsets.UTF_8)
            .split("\n")
            .toList()
            .forEach {
                cityList.add(CityChoiceItem(it))
            }
        return cityList
    }
}