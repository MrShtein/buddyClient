package mr.shtein.buddyandroidclient.di.module

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module {
    single { provideFusedLocationClient(androidContext())}
    single { CancellationTokenSource() }
}

private fun provideFusedLocationClient(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)