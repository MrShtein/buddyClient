package mr.shtein.buddyandroidclient.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.retrofit.RetrofitServices
import retrofit2.Response

class AnimalRepository {

    private val retrofit: RetrofitServices = Common.retrofitService

    public suspend fun getAnimals() : List<Animal> = withContext(Dispatchers.IO) {
        val result = retrofit.getAnimals()
        when (result.code()) {
            200 -> {
                return@withContext result.body() ?: listOf()
            }
            500 -> {
                throw ServerErrorException()
            }
            else -> {
                return@withContext listOf()
            }
        }
    }

    

}