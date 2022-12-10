package mr.shtein.buddyandroidclient.di.module

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mr.shtein.data.db.CityDbHelper
import mr.shtein.util.validator.PasswordEmptyFieldValidator
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule: Module = module {
    single { provideFusedLocationClient(androidContext())}
    single { CancellationTokenSource() }
    factory { CityDbHelper(androidContext(), get()) }
    factory { PasswordEmptyFieldValidator(get(), provideCoroutineScope()) }
}

private fun provideFusedLocationClient(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)

private fun provideCoroutineScope() = CoroutineScope(Dispatchers.Main)
