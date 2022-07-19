package mr.shtein.buddyandroidclient.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.retrofit.RetrofitServices
import retrofit2.Response

class AuthenticationRepository {

    private val retrofit: RetrofitServices = Common.retrofitService

    public suspend fun resetPassword(email: String) : String? = withContext(Dispatchers.IO) {
        val result = retrofit.resetPassword(email)
        return@withContext result.body()
    }

}