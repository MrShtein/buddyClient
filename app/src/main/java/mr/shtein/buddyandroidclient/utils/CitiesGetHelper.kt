package mr.shtein.buddyandroidclient.utils

import android.util.Log
import kotlinx.coroutines.*
import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem
import mr.shtein.buddyandroidclient.retrofit.Common

class CitiesGetHelper(
    var lettersCount:Int
) {

     var coroutineScope = CoroutineScope(Dispatchers.Main + Job())
     var cityChoiceItems: List<CityChoiceItem> = mutableListOf()

    init {
        coroutineScope.launch {
            getCitiesFromServer()
        }
    }




    fun onCitiesChanged(letters: String, cityChoiceItems: List<CityChoiceItem>)
    : List<CityChoiceItem> {
        var newCityChoiceItemList: List<CityChoiceItem> = mutableListOf()
        when {
            letters.isEmpty() -> {
                newCityChoiceItemList = getCities()
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
                    cityChoiceItems.filter {
                        it.name.lowercase().startsWith(letters.lowercase())
                    }
                }
                lettersCount++
            }
        }
        return newCityChoiceItemList
    }

    fun getCities(): List<CityChoiceItem> {
        return listOf(
            CityChoiceItem(749, "Москва", "Московская область"),
            CityChoiceItem(1023, "Санкт-Петербург", "Ленинградская область"),
            CityChoiceItem(832, "Новосибирск", "Новосибирская область"),
            CityChoiceItem(337, "Екатеринбург", "Свердловская область"),
            CityChoiceItem(443, "Казань", "Республика Татарстан"),
            CityChoiceItem(799, "Нижний Новгород", "Нижегородская область"),
            CityChoiceItem(1285, "Челябинск", "Челябинская область"),
            CityChoiceItem(1020, "Самара", "Самарская область"),
            CityChoiceItem(875, "Омск", "Омская область"),
            CityChoiceItem(1002, "Ростов-на-Дону", "Ростовская область"),
            CityChoiceItem(1381, "Уфа", "Республика Башкортостан"),
            CityChoiceItem(597, "Красноярск", "Красноярский край"),
            CityChoiceItem(224, "Воронеж", "Воронежская область"),
            CityChoiceItem(208, "Волгоград", "Волгоградская область"),
            CityChoiceItem(584, "Краснодар", "Краснодарский край"),
            CityChoiceItem(1027, "Саратов", "Саратовская область"),
            CityChoiceItem(1210, "Тюмень", "Тюменская область"),
            CityChoiceItem(1183, "Тольятти", "Самарская область"),
            CityChoiceItem(411, "Ижевск", "Удмуртская республика"),
            CityChoiceItem(95, "Барнаул", "Алтайский край"),
            CityChoiceItem(1222, "Ульяновск", "Ульяновская область"),
            CityChoiceItem(429, "Иркутск", "Иркутская область"),
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