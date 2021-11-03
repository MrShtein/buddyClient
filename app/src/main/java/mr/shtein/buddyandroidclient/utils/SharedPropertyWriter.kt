package mr.shtein.buddyandroidclient.utils

import android.content.Context

class SharedPropertyStringWriter {
    fun write(storageName: String, prefName: String, pref: String, context: Context) {
        val sharedPref = context.getSharedPreferences(storageName, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(prefName, pref)
            apply()
        }
    }
}