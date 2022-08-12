package mr.shtein.buddyandroidclient.domain.interactor

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.exceptions.validate.LocationServiceException
import mr.shtein.buddyandroidclient.model.Coordinates
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationServiceInteractor() : LocationInteractor, KoinComponent {

    private val fusedLocationClient: FusedLocationProviderClient by inject()
    private val cancelTokenSourceForLocation: CancellationTokenSource by inject()
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun getCurrentDistance(): Coordinates = withContext(Dispatchers.IO) {
        val locationTask = fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancelTokenSourceForLocation.token
        )
        val location = getLocation()
        location?.let {
            return@withContext Coordinates(it.latitude, it.longitude)
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