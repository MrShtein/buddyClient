package mr.shtein.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.BadTokenException
import mr.shtein.data.exception.EmptyBodyException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.model.Breed
import mr.shtein.network.NetworkService

private const val SERVER_ERROR = "Что-то не так с сервером, попробуйте позже"

class NetworkAnimalBreedRepository(
    val networkService: NetworkService
) : AnimalBreedRepository {
    override suspend fun getAnimalBreeds(animalTypeId: Int): List<Breed> =
        withContext(Dispatchers.IO) {
            val result = networkService.getAnimalsBreed(animalType = animalTypeId)
            return@withContext when(result.code()) {
                200 -> {
                    result.body() ?: throw EmptyBodyException(SERVER_ERROR)
                }
                403 -> {
                    throw BadTokenException()
                }
                else -> {
                    throw ServerErrorException()
                }
            }
        }
}