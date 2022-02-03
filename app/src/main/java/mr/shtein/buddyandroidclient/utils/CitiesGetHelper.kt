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




    fun onCitiesChanged(letters: String, cityChoiceItems: List<CityChoiceItem>): List<CityChoiceItem> {
        var newCityChoiceItemList: List<CityChoiceItem> = mutableListOf()
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