package mr.shtein.buddyandroidclient.utils

import android.content.Context

class SharedPropertyLongWriter {
    fun write(storageName: String, prefName: String, pref: Long, context: Context) {
        val sharedPref = context.getSharedPreferences(storageName, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong(prefName, pref)
            apply()
        }
    }
}