package mr.shtein.buddyandroidclient.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import mr.shtein.buddyandroidclient.data.repository.DatabasePropertiesRepository
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileOutputStream

const val DATABASE_NAME = "buddy"
const val DATABASE_VERSION = 1
const val ASSETS_PATH = "databases"

class CityDbHelper(val context: Context) : SQLiteOpenHelper (
    context, DATABASE_NAME, null, DATABASE_VERSION
), KoinComponent {
    private val databasePropertiesRepository: DatabasePropertiesRepository by inject()

    private fun installDatabaseIsOutdated(): Boolean {
        return databasePropertiesRepository.getDatabaseVersion() < DATABASE_VERSION
    }

    private fun writeDatabaseInfoInPreference() {
        databasePropertiesRepository.saveDatabaseVersion(DATABASE_VERSION)
        databasePropertiesRepository.saveDatabaseName(DATABASE_NAME)
    }

    private fun installOrUpdateIfNecessary() {
        if (installDatabaseIsOutdated()) {
            context.deleteDatabase(DATABASE_NAME)
            installDatabaseFromAssets()
            writeDatabaseInfoInPreference()
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        throw RuntimeException("The $DATABASE_NAME database is not writable.")
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getReadableDatabase()
    }

    private fun installDatabaseFromAssets() {
        val inputStream = context.assets.open("$ASSETS_PATH/$DATABASE_NAME.sqlite")

        try {
            val outputFile = File(context.getDatabasePath(DATABASE_NAME).path)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()

            outputStream.flush()
            outputStream.close()
        } catch (exception: Throwable) {
            throw RuntimeException("The $DATABASE_NAME database couldn't be installed.", exception)
        }
    }


    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}

