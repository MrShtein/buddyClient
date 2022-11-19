package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.model.EmailCheckRequest
import mr.shtein.model.LoginResponse
import mr.shtein.model.Person

interface UserRepository {
    suspend fun resetPassword(email: String) : String?
    suspend fun signIn(person: Person): LoginResponse
    suspend fun signUp(person: Person)
    suspend fun isEmailExist(emailCheckRequest: EmailCheckRequest): Boolean
}