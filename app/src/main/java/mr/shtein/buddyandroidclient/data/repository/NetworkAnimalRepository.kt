package mr.shtein.buddyandroidclient.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.retrofit.NetworkService

class NetworkAnimalRepository(private val networkService: NetworkService) : AnimalRepository {

    override suspend fun getAnimals(animalTypeList: List<Int>): List<Animal> =
        withContext(Dispatchers.IO) {
            val result = networkService.getAnimals(animalTypeList)
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