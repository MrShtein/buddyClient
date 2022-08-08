package mr.shtein.buddyandroidclient.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.exceptions.validate.IncorrectDataException
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.LoginResponse
import mr.shtein.buddyandroidclient.model.Person
import mr.shtein.buddyandroidclient.retrofit.RetrofitService

interface UserRepository {
    suspend fun resetPassword(email: String) : String?
    suspend fun signIn(person: Person): LoginResponse
    suspend fun signUp(person: Person)
}

class RetrofitUserRepository(private val retrofitService: RetrofitService): UserRepository {

    override suspend fun resetPassword(email: String) : String? = withContext(Dispatchers.IO) {
        val result = retrofitService.resetPassword(email)
        return@withContext result.body()
    }

    override suspend fun signIn(person: Person): LoginResponse = withContext(Dispatchers.IO) {
        val result = retrofitService.loginUser(person)
        when(result.code()) {
            200 -> {
                return@withContext result.body() !!
            }
            404 -> {
                throw IncorrectDataException()
            }
            else -> {
                throw ServerErrorException()
            }
        }
    }

    override suspend fun signUp(person: Person) = withContext(Dispatchers.IO) {
        val result = retrofitService.registerUser(person)
        when(result.code()) {
            201 -> {
                return@withContext
            }
            400 -> {
                throw IncorrectDataException()
            }
            else -> throw ServerErrorException()
        }
    }
}