package mr.shtein.data.repository

import mr.shtein.data.util.SharedPreferences

class SharedAppPropertiesRepository(private val storage: SharedPreferences) : AppPropertiesRepository {
    override fun getBottomNavHeight(): Int {
        return storage.readInt(BOTTOM_NAV_HEIGHT_KEY, 0)
    }

    override fun saveBottomNavHeight(bottomNavHeight: Int) {
        storage.writeInt(BOTTOM_NAV_HEIGHT_KEY, bottomNavHeight)
    }

    override fun removeAll() {
        storage.cleanAllData()
    }

    companion object {
        const val BOTTOM_NAV_HEIGHT_KEY = "bottom_nav_height"
    }
}