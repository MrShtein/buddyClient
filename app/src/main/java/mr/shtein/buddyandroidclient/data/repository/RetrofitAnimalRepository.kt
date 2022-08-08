package mr.shtein.buddyandroidclient.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.retrofit.RetrofitService

interface AnimalRepository {
    suspend fun getAnimals(animalTypeList: List<Int>): List<Animal>
}

class RetrofitAnimalRepository(private val retrofitService: RetrofitService) : AnimalRepository {

    override suspend fun getAnimals(animalTypeList: List<Int>): List<Animal> =
        withContext(Dispatchers.IO) {
            val result = retrofitService.getAnimals(animalTypeList)
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