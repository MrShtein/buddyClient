package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.LoginResponse
import mr.shtein.buddyandroidclient.model.Person

interface UserRepository {
    suspend fun resetPassword(email: String) : String?
    suspend fun signIn(person: Person): LoginResponse
    suspend fun signUp(person: Person)
}