package mr.shtein.buddyandroidclient.data.repository

import android.content.Context
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.math.log

const val USER_TOKEN_KEY = "token_key"
const val USER_ID_KEY = "id"
const val USER_LOGIN_KEY = "user_login"
const val USER_ROLE_KEY = "user_role"
const val USER_NAME_KEY = "user_name"
const val USER_SURNAME_KEY = "user_surname"
const val USER_PHONE_NUMBER_KEY = "user_phone_number"
const val USER_GENDER_KEY = "user_gender"
const val USER_CITY_KEY = "user_city"
const val USER_AVATAR_URI_KEY = "user_avatar_uri"
const val IS_LOCKED_KEY = "is_locked"
const val USER_STORE_NAME = "userStore"

class SharedUserPropertiesRepository()  : UserPropertiesRepository, KoinComponent {

    private val storage: SharedPreferences by inject(qualifier =  named(USER_STORE_NAME))

    override fun getUserToken(): String {
        return storage.readString(USER_TOKEN_KEY, "")
    }

    override fun saveUserToken(token: String) {
       storage.writeString(USER_TOKEN_KEY, token)
    }

    override fun getUserId(): Long {
        return storage.readLong(USER_ID_KEY, 0)
    }

    override fun saveUserId(userId: Long) {
        storage.writeLong(USER_ID_KEY, userId)
    }

    override fun getUserLogin(): String {
        return storage.readString(USER_LOGIN_KEY, "")
    }

    override fun saveUserLogin(login: String) {
        storage.writeString(USER_LOGIN_KEY, login)
    }

    override fun getUserRole(): String {
        return storage.readString(USER_ROLE_KEY, "")
    }

    override fun saveUserRole(userRole: String) {
        storage.writeString(USER_ROLE_KEY, userRole)
    }

    override fun getUserName(): String {
        return storage.readString(USER_NAME_KEY, "")
    }

    override fun saveUserName(userName: String) {
        storage.writeString(USER_NAME_KEY, userName)
    }

    override fun getUserSurname(): String {
        return storage.readString(USER_SURNAME_KEY, "")
    }

    override fun saveUserSurname(userSurname: String) {
        storage.writeString(USER_SURNAME_KEY, userSurname)
    }

    override fun getUserPhoneNumber(): String {
        return storage.readString(USER_PHONE_NUMBER_KEY, "")
    }

    override fun saveUserPhoneNumber(phoneNumber: String) {
        storage.writeString(USER_PHONE_NUMBER_KEY, phoneNumber)
    }

    override fun getUserGender(): String {
        return storage.readString(USER_GENDER_KEY, "")
    }

    override fun saveUserGender(userGender: String) {
        storage.writeString(USER_GENDER_KEY, userGender)
    }

    override fun getUserCity(): String {
        return storage.readString(USER_CITY_KEY, "")
    }

    override fun saveUserCity(userCity: String) {
        storage.writeString(USER_CITY_KEY, userCity)
    }

    override fun getUserUriKey(): String {
        return storage.readString(USER_AVATAR_URI_KEY, "")
    }

    override fun saveUserUriKey(userUriKey: String) {
        storage.writeString(USER_AVATAR_URI_KEY, userUriKey)
    }

    override fun isUserLocked(): Boolean {
        return storage.readBoolean(IS_LOCKED_KEY, false)
    }

    override fun saveIsUserLocked(isUserLocked: Boolean) {
        storage.writeBoolean(IS_LOCKED_KEY, isUserLocked)
    }

    override fun removeAll() {
        storage.cleanAllData()
    }
}