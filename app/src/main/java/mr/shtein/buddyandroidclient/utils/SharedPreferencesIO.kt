package mr.shtein.buddyandroidclient.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPropertyWriter(val context: Context, private val storageName: String) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(storageName, Context.MODE_PRIVATE)

    fun writeString(prefName: String, pref: String) {
        with(sharedPref.edit()) {
            putString(prefName, pref)
            apply()
        }
    }

    fun writeInt(prefName: String, pref: Int) {
        with(sharedPref.edit()) {
            putInt(prefName, pref)
            apply()
        }
    }

    fun writeLong(prefName: String, pref: Long) {
        with(sharedPref.edit()) {
            putLong(prefName, pref)
            apply()
        }
    }

    fun writeBoolean(prefName: String, pref: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(prefName, pref)
            apply()
        }
    }
}