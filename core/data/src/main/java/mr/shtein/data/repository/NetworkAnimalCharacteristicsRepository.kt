package mr.shtein.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.BadTokenException
import mr.shtein.data.exception.EmptyBodyException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.model.AnimalCharacteristic
import mr.shtein.network.NetworkService

private const val COLOR_ID = 1
private const val SERVER_ERROR = "Что-то не так с сервером, попробуйте позже"

class NetworkAnimalCharacteristicsRepository(
    val networkService: NetworkService
) : AnimalCharacteristicsRepository {
    override suspend fun getAnimalColors(): List<AnimalCharacteristic> =
        withContext(Dispatchers.IO) {
            val result = networkService.getAnimalsCharacteristicByCharacteristicTypeId(
                characteristicId = COLOR_ID
            )
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