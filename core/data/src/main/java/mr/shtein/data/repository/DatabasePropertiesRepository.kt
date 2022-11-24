package mr.shtein.data.repository

interface DatabasePropertiesRepository {
    fun getDatabaseVersion(): Int
    fun saveDatabaseVersion(databaseVersion: Int)

    fun getDatabaseName(): String
    fun saveDatabaseName(databaseName: String)

    fun removeAll()
}