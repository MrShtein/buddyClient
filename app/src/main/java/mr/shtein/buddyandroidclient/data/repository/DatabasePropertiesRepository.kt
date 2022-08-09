package mr.shtein.buddyandroidclient.data.repository

interface DatabasePropertiesRepository {
    fun getDatabaseVersion(): String
    fun saveDatabaseVersion(databaseVersion: String)

    fun getDatabaseName(): String
    fun saveDatabaseName(databaseName: String)

    fun removeAll()
}