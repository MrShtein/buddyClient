package mr.shtein.data.repository

import android.security.keystore.KeyNotYetValidException
import mr.shtein.data.util.SharedPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier


const val DATABASE_VERSION = "database_version"
const val DATABASE_NAME = "database_name"


class SharedDatabasePropertiesRepository(private val storage: SharedPreferences) : DatabasePropertiesRepository {

    override fun getDatabaseVersion(): Int {
        return storage.readInt(DATABASE_VERSION, 0)
    }

    override fun saveDatabaseVersion(databaseVersion: Int) {
        storage.writeInt(DATABASE_VERSION, databaseVersion)
    }

    override fun getDatabaseName(): String {
        return storage.readString(DATABASE_NAME, "")
    }

    override fun saveDatabaseName(databaseName: String) {
        storage.writeString(DATABASE_NAME, databaseName)
    }

    override fun removeAll() {
        storage.cleanAllData()
    }
}