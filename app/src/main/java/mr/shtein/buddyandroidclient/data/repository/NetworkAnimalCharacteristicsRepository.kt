package mr.shtein.buddyandroidclient.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.exceptions.validate.BadTokenException
import mr.shtein.buddyandroidclient.exceptions.validate.EmptyBodyException
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.model.AnimalCharacteristic
import mr.shtein.buddyandroidclient.retrofit.NetworkService

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