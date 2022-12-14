package mr.shtein.buddyandroidclient.domain.interactor

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.animal.domain.LocationInteractor
import mr.shtein.data.exception.LocationServiceException

class LocationServiceInteractor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val cancelTokenSourceForLocation: CancellationTokenSource
    ) : LocationInteractor {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun getCurrentDistance(): mr.shtein.data.model.Coordinates = withContext(Dispatchers.IO) {
        val locationTask = fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancelTokenSourceForLocation.token
        )
        val location = getLocation()
        location?.let {
            return@withContext mr.shtein.data.model.Coordinates(it.latitude, it.longitude)
        }

        throw LocationServiceException()
    }

    private suspend fun getLocation(): Location? {
        return Tasks.await(
            fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancelTokenSourceForLocation.token
            )
        )
    }
}