package mr.shtein.buddyandroidclient.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.Coordinates
import mr.shtein.network.NetworkService

class NetworkDistanceCounterRepository(private val networkService: NetworkService) :
    DistanceCounterRepository {

    override suspend fun getDistancesFromUser(
        token: String,
        coordinates: Coordinates
    ): HashMap<Int, Int> = withContext(Dispatchers.IO) {
        val result =
            networkService.getAllDistances(token, coordinates.latitude, coordinates.longitude)
        when (result.code()) {
            200 -> {
                return@withContext result.body() ?: hashMapOf()
            }
            500 -> {
                throw ServerErrorException()
            }
            else -> {
                return@withContext hashMapOf()
            }
        }
    }
}