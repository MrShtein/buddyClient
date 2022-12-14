package mr.shtein.data.repository

interface AppPropertiesRepository {
    fun getBottomNavHeight(): Int
    fun saveBottomNavHeight(bottomNavHeight: Int)

    fun removeAll()

}