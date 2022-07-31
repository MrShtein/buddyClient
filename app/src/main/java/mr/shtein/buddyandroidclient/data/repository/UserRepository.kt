package mr.shtein.buddyandroidclient.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.exceptions.validate.IncorrectDataException
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.LoginResponse
import mr.shtein.buddyandroidclient.model.Person
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.retrofit.RetrofitServices
import retrofit2.Response

class UserRepository {

    private val retrofit: RetrofitServices = Common.retrofitService

    public suspend fun resetPassword(email: String) : String? = withContext(Dispatchers.IO) {
        val result = retrofit.resetPassword(email)
        return@withContext result.body()
    }

    public suspend fun signIn(person: Person): LoginResponse = withContext(Dispatchers.IO) {
        val result = retrofit.loginUser(person)
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

    public suspend fun signUp(person: Person) = withContext(Dispatchers.IO) {
        val result = retrofit.registerUser(person)
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