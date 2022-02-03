package mr.shtein.buddyandroidclient.screens

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.CitiesAdapter
import mr.shtein.buddyandroidclient.adapters.OnCityListener
import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.utils.CityCallback
import mr.shtein.buddyandroidclient.utils.SharedPreferences

private const val MAG = "City"
private const val MAIN_CITY_NAME_PREF = "main_city"

class CityChoiceFragment : Fragment(R.layout.city_choice_fragment), OnCityListener {

    private lateinit var cityChoiceItems: List<CityChoiceItem>
    private lateinit var adapter: CitiesAdapter
    private lateinit var list: RecyclerView
    private var lettersCount = 0
    private var isCitiesVisible = false
    private var coroutineScope = CoroutineScope(Dispatchers.Main + Job())


    override fun onDestroy() {
        super.onDestroy()
        Log.d(MAG, "City Fragment destroyed")
        coroutineScope.cancel()
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

        coroutineScope.launch {
            getCitiesFromServer()
        }


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
                        it.name.lowercase().startsWith(letters.lowercase())
                    }
                }
                else -> {
                    newCityChoiceItemList = if (lettersCount == 0) {
                        cityChoiceItems.filter {
                            it.name.lowercase().startsWith(letters.lowercase())
                        }
                    } else {
                        adapter.cityChoiceItems.filter {
                            it.name.lowercase().startsWith(letters.lowercase())
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

        val cityInput = view.findViewById<TextInputEditText>(R.id.input_text_for_city_choice)

        cityInput.onFocusChangeListener = object : View.OnFocusChangeListener {

            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                val context: Context? = v?.context
                if (hasFocus) {
                    list = view.findViewById(R.id.city_list)
                    list.setPadding(0, 10, 0, 0)
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
                    //animateCitiesAlpha(list)
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
        val sharedPropertyWriter =
            SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        val cityForStorage = makeStringForCityStorage(adapter.cityChoiceItems[position])
        sharedPropertyWriter.writeString(SharedPreferences.USER_CITY_KEY, cityForStorage)
        findNavController().navigate(R.id.action_cityChoiceFragment_to_bottomNavFragment)

    }

    private fun makeStringForCityStorage(city: CityChoiceItem): String {
        return "${city.city_id},${city.name},${city.region}"
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
            CityChoiceItem(752, "Москва", "Московская область"),
            CityChoiceItem(1028, "Санкт-Петербург", "Ленинградская область"),
            CityChoiceItem(835, "Новосибирск", "Новосибирская область"),
            CityChoiceItem(339, "Екатеринбург", "Свердловская область"),
            CityChoiceItem(445, "Казань", "Республика Татарстан"),
            CityChoiceItem(802, "Нижний Новгород", "Нижегородская область"),
            CityChoiceItem(1291, "Челябинск", "Челябинская область"),
            CityChoiceItem(1025, "Самара", "Самарская область"),
            CityChoiceItem(879, "Омск", "Омская область"),
            CityChoiceItem(1006, "Ростов-на-Дону", "Ростовская область"),
            CityChoiceItem(1387, "Уфа", "Республика Башкортостан"),
            CityChoiceItem(600, "Красноярск", "Красноярский край"),
            CityChoiceItem(225, "Воронеж", "Воронежская область"),
            CityChoiceItem(209, "Волгоград", "Волгоградская область"),
            CityChoiceItem(586, "Краснодар", "Краснодарский край"),
            CityChoiceItem(1032, "Саратов", "Саратовская область"),
            CityChoiceItem(1215, "Тюмень", "Тюменская область"),
            CityChoiceItem(1188, "Тольятти", "Самарская область"),
            CityChoiceItem(413, "Ижевск", "Удмуртская республика"),
            CityChoiceItem(96, "Барнаул", "Алтайский край"),
            CityChoiceItem(1227, "Ульяновск", "Ульяновская область"),
            CityChoiceItem(431, "Иркутск", "Иркутская область"),
        )
    }

    private suspend fun getCitiesFromServer() = withContext(Dispatchers.IO + Job()) {
        val retrofitService = Common.retrofitService
        try {
            val cityResponse = retrofitService.getAllCities().execute()
            if (cityResponse.isSuccessful && cityResponse.body() != null) {
                cityChoiceItems = cityResponse.body()!!.citiesList
            } else {
                cityChoiceItems = getCities()
            }
        } catch (e: Exception) {
            cityChoiceItems = getCities()
            Log.e("ERROR", "Что-то не так с сетью")
        }


    }


}