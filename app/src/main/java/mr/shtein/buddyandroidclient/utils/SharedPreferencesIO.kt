package mr.shtein.buddyandroidclient.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesIO(val context: Context, private val storageName: String) {

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

    companion object {
        const val IS_FROM_REGISTRATION_KEY = "is_from_registration"
        const val PERSISTENT_STORAGE_NAME: String = "buddy_storage"
        const val TOKEN_KEY = "token_key"
        const val USER_ID_KEY = "id"
        const val USER_LOGIN_KEY = "user_login"
        const val USER_ROLE_KEY = "user_role"
        const val IS_LOCKED_KEY = "is_locked"
        const val USER_NAME_KEY = "user_name"
        const val USER_SURNAME_KEY = "user_surname"
        const val USER_PHONE_NUMBER_KEY = "user_phone_number"
        const val USER_GENDER_KEY = "user_gender"
        const val USER_CITY_KEY = "user_city"
        const val USER_REGION_KEY = "user_region"
        const val LATITUDE_KEY = "latitude"
        const val ATTITUDE_KEY = "attitude"
        const val PHONE_NUMBER_KEY = "user_phone_number"
    }
}