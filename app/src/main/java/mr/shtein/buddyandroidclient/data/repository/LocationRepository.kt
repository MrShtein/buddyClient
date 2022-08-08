package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.Coordinates

interface LocationRepository {
    suspend fun getDistancesFromUser(token: String, coordinates: Coordinates): HashMap<Int, Int>
}