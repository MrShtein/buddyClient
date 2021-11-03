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
import mr.shtein.buddyandroidclient.utils.SharedPreferencesIO

private const val MAG = "City"
private const val MAIN_CITY_NAME_PREF = "main_city"
private const val PERSISTENT_STORAGE_NAME: String = "buddy_storage"

class CityChoiceFragment : Fragment(), OnCityListener {

    private lateinit var cityChoiceItems: List<CityChoiceItem>
    private lateinit var adapter: CitiesAdapter
    private lateinit var list: RecyclerView
    private var lettersCount = 0
    private var isCitiesVisible = false



    override fun onDestroy() {
        super.onDestroy()
        Log.d(MAG, "City Fragment destroyed")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(MAG, "City Fragment view create")
        return inflater.inflate(R.layout.city_choice_fragment, container, false)
    }

    override fun onPause() {
        super.onPause()
        Log.d(MAG, "City Fragment view PAUSED")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(MAG, "City Fragment Attache")
    }

    override fun onStop() {
        super.onStop()
        Log.d(MAG, "City Fragment view STOPPED")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(MAG, "City Fragment view created")
        adapter = context?.let { CitiesAdapter(it, getCities(), this) }!!

        cityChoiceItems = getCitiesFromFile("cities.txt", this.requireContext())

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
                    newCityChoiceItemList = if (lettersCount == 0) {
                        cityChoiceItems.filter {
                            it.cityName.lowercase().startsWith(letters.lowercase())
                        }
                    } else {
                        adapter.cityChoiceItems.filter {
                            it.cityName.lowercase().startsWith(letters.lowercase())
                        }
                    }
                    lettersCount++
                }
            }

            val callback = CityCallback(adapter.cityChoiceItems, newCityChoiceItemList)
            val diff = DiffUtil.calculateDiff(callback)
            diff.dispatchUpdatesTo(adapter)
            if (lettersCount == 1) {
                animateCitiesAlpha(list)
            }
            adapter.cityChoiceItems = newCityChoiceItemList
        }

        val cityInput = view.findViewById<EditText>(R.id.city_search)

        cityInput.onFocusChangeListener = object : View.OnFocusChangeListener {

            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                val context: Context? = v?.context
                if (hasFocus) {
                    list = view.findViewById(R.id.city_list)
                    list.setPadding(0, 10, 0,0)
                    list.adapter = adapter
                    list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }
            }

        }
        cityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isCitiesVisible) {
                  //  animateCitiesAlpha(list)
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
        val sharedPropertyWriter = SharedPreferencesIO(requireContext(), PERSISTENT_STORAGE_NAME)
        val bundle = Bundle()
        val cityName = adapter.cityChoiceItems[position].cityName
        bundle.putString("city", cityName)
        sharedPropertyWriter.writeString(MAIN_CITY_NAME_PREF, cityName)
        findNavController().navigate(R.id.animal_choice_fragment, bundle)

    }

    private fun animateCitiesAlpha(list: RecyclerView) {
        ObjectAnimator
            .ofFloat(list, "alpha", 1f).apply {
                duration = 250
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
            .toSet()
            .forEach {
                cityList.add(CityChoiceItem(it))
            }
        return cityList
    }
}