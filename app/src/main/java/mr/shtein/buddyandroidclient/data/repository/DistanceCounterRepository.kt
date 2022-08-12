package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.Coordinates

interface DistanceCounterRepository {
    suspend fun getDistancesFromUser(token: String, coordinates: Coordinates): HashMap<Int, Int>
}