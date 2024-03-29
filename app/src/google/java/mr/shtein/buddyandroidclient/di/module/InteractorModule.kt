package mr.shtein.buddyandroidclient.di.module

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import mr.shtein.animal.domain.AnimalInteractor
import mr.shtein.animal.domain.AnimalInteractorImpl
import mr.shtein.animal.domain.LocationInteractor
import mr.shtein.buddyandroidclient.domain.interactor.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val locationServiceModule: Module = module {
    single<AnimalInteractor> { AnimalInteractorImpl(get(), get(), get(), get(), get(), get()) }
    single<LocationInteractor> {
        LocationServiceInteractor(
            provideFusedLocationClient(androidContext()),
            provideCancellationTokenSource()
        )
    }
}

private fun provideFusedLocationClient(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)

private fun provideCancellationTokenSource() = CancellationTokenSource()


