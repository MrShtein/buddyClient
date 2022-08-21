package mr.shtein.buddyandroidclient.domain.interactor

import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.huawei.hmf.tasks.CancellationTokenSource
import com.huawei.hmf.tasks.Task
import com.huawei.hmf.tasks.Tasks
import com.huawei.hms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.exceptions.validate.LocationServiceException
import mr.shtein.buddyandroidclient.model.Coordinates
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalListPresenter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

const val HUAWEI_DEBUG = "huawei_debug"

class LocationServiceInteractor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest,
) : LocationInteractor {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    var result: Coordinates? = null

    override suspend fun getCurrentDistance(): Coordinates = withContext(Dispatchers.IO) {
        val location: Location = getLocation()
        result = Coordinates(location.latitude, location.longitude)
        if (result != null) {
            return@withContext result!!
        }
        return@withContext Coordinates(55.951351, 37.35018)
    }

    private suspend fun getLocation(): Location {
        return Tasks.await(fusedLocationClient.lastLocation)
    }
}