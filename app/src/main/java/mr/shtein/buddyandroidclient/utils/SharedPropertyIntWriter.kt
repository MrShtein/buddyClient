package mr.shtein.buddyandroidclient.utils

import android.content.Context

class SharedPropertyIntWriter {
    fun write(storageName: String, prefName: String, pref: Int, context: Context) {
        val sharedPref = context.getSharedPreferences(storageName, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(prefName, pref)
            apply()
        }
    }
}