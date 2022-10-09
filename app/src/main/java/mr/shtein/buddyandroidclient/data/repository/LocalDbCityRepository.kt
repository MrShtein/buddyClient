package mr.shtein.buddyandroidclient.data.repository

import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.db.CityDbHelper
import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem

class LocalDbCityRepository(
    val cityDbHelper: CityDbHelper,
    val databasePropertiesRepository: DatabasePropertiesRepository
) : CityRepository {
    override suspend fun getCities(): MutableList<CityChoiceItem> = withContext(Dispatchers.IO) {
        val db = cityDbHelper.readableDatabase
        val databaseName = databasePropertiesRepository.getDatabaseName()
        val cursor = db.query(
            databaseName,
            null, null, null,
            null, null, null, null
        )
        return@withContext makeCityChoiceItemsFromCursor(cursor)
    }

    private fun makeCityChoiceItemsFromCursor(cursor: Cursor): MutableList<CityChoiceItem> {
        val cityChoiceItemsList = arrayListOf<CityChoiceItem>()
        with(cursor) {
            while (moveToNext()) {
                val cityChoiceItem = CityChoiceItem(
                    getInt(getColumnIndexOrThrow("id")),
                    getString(getColumnIndexOrThrow("city_name")),
                    getString(getColumnIndexOrThrow("region_name"))
                )
                cityChoiceItemsList.add(cityChoiceItem)
            }
        }
        return cityChoiceItemsList
    }
}