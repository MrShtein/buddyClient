package mr.shtein.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.EmptyBodyException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.model.AnimalType
import mr.shtein.network.NetworkService

private const val SERVER_ERROR = "Что-то не так с сервером, попробуйте позже"

class NetworkAnimalTypeRepository(
    private val networkService: NetworkService
) : AnimalTypeRepository {
    override suspend fun getAnimalTypes(): List<AnimalType> = withContext(Dispatchers.IO) {
        val result = networkService.getAnimalsTypes()
        return@withContext when(result.code()) {
            200 -> {
                result.body() ?: throw EmptyBodyException(SERVER_ERROR)
            }
            else -> throw ServerErrorException()
        }
    }
}