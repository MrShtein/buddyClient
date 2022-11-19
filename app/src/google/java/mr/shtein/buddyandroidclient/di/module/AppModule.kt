package mr.shtein.buddyandroidclient.di.module

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mr.shtein.buddyandroidclient.BuildConfig
import mr.shtein.buddyandroidclient.db.CityDbHelper
import mr.shtein.buddyandroidclient.utils.PasswordEmptyFieldValidator
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule: Module = module {
    single { provideFusedLocationClient(androidContext())}
    single { CancellationTokenSource() }
    factory { CityDbHelper(androidContext(), get()) }
    factory { PasswordEmptyFieldValidator(get(), provideCoroutineScope()) }
    single(named("userStore")) { provideUserStore(androidContext()) }
    single(named("kennelStore")) { provideKennelStore(androidContext()) }
    single(named("databaseStore")) { provideDatabaseStore(androidContext()) }
    single(named("filterStore")) { provideFilterStore(androidContext()) }

}

private fun provideUserStore(context: Context) =
    SharedPreferences(context, BuildConfig.USER_STORAGE_NAME)

private fun provideKennelStore(context: Context) =
    SharedPreferences(context, BuildConfig.KENNEL_STORAGE_NAME)

private fun provideDatabaseStore(context: Context) =
    SharedPreferences(context, BuildConfig.DATABASE_STORAGE_NAME)

private fun provideFilterStore(context: Context) =
    SharedPreferences(context, BuildConfig.FILTER_STORAGE_NAME)

private fun provideFusedLocationClient(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)

private fun provideCoroutineScope() = CoroutineScope(Dispatchers.Main)
