package mr.shtein.data.repository

import mr.shtein.data.model.Coordinates


interface DistanceCounterRepository {
    suspend fun getDistancesFromUser(token: String, coordinates: Coordinates): HashMap<Int, Int>
}