package mr.shtein.buddyandroidclient.utils

import android.content.Context

class SharedPropertyBooleanWriter {
    fun write(storageName: String, prefName: String, pref: Boolean, context: Context) {
        val sharedPref = context.getSharedPreferences(storageName, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(prefName, pref)
            apply()
        }
    }
}