package mr.shtein.data.repository

interface UserPropertiesRepository {
    fun getUserToken(): String
    fun saveUserToken(token: String)

    fun getUserId(): Long
    fun saveUserId(userId: Long)

    fun getUserLogin(): String
    fun saveUserLogin(login: String)

    fun getUserRole(): String
    fun saveUserRole(userRole: String)

    fun getUserName(): String
    fun saveUserName(userName: String)

    fun getUserSurname(): String
    fun saveUserSurname(userSurname: String)

    fun getUserPhoneNumber(): String
    fun saveUserPhoneNumber(phoneNumber: String)

    fun getUserGender(): String
    fun saveUserGender(userGender: String)

    fun getUserCity(): String
    fun saveUserCity(userCity: String)

    fun getUserUri(): String
    fun saveUserUri(userUriKey: String)

    fun isUserLocked(): Boolean
    fun saveIsUserLocked(isUserLocked: Boolean)

    fun removeAll()

}