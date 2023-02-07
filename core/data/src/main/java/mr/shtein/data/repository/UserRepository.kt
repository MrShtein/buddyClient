package mr.shtein.data.repository

import mr.shtein.model.*

interface UserRepository {
    suspend fun resetPassword(email: String): String?
    suspend fun signIn(person: Person): LoginResponse
    suspend fun signUp(person: Person)
    suspend fun updatePersonInfo(
        header: HashMap<String, String>,
        personRequest: PersonRequest
    ): PersonResponse

    suspend fun isEmailExist(emailCheckRequest: EmailCheckRequest): Boolean
    suspend fun checkOldPassword(
        headerMap: Map<String, String>,
        passwordCheckRequest: PasswordCheckRequest
    ): Boolean
    suspend fun addBidToBecomeVolunteer(token: String, kennelId: Int)
}