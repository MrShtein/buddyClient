package mr.shtein.data.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(val context: Context, private val storageName: String) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(storageName, Context.MODE_PRIVATE)

    fun writeString(prefName: String, pref: String) {
        with(sharedPref.edit()) {
            putString(prefName, pref)
            apply()
        }
    }

    fun readString(prefName: String, pref: String): String {
        val data = sharedPref.getString(prefName, pref) ?: ""
        return data
    }

    fun writeInt(prefName: String, pref: Int) {
        with(sharedPref.edit()) {
            putInt(prefName, pref)
            apply()
        }
    }

    fun readInt(prefName: String, pref: Int): Int {
        return sharedPref.getInt(prefName, pref)
    }

    fun writeLong(prefName: String, pref: Long) {
        with(sharedPref.edit()) {
            putLong(prefName, pref)
            apply()
        }
    }

    fun readLong(prefName: String, pref: Long): Long {
        return sharedPref.getLong(prefName, pref)
    }

    fun writeBoolean(prefName: String, pref: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(prefName, pref)
            apply()
        }
    }

    fun readBoolean(prefName: String, pref: Boolean): Boolean {
        return sharedPref.getBoolean(prefName, pref)
    }

    fun readStringSet(prefName: String, pref: Set<String>?): MutableSet<String>? {
        return sharedPref.getStringSet(prefName, pref)
    }

    fun writeStringSet(prefName: String, pref: Set<String>) {
        with(sharedPref.edit()) {
            putStringSet(prefName, pref)
            apply()
        }
    }

    fun cleanAllData() {
        sharedPref
            .edit()
            .clear()
            .apply()
    }

    companion object {



    }
}