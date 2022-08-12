package mr.shtein.buddyandroidclient.di.module

import android.app.Activity
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.GsonBuilder
import mr.shtein.buddyandroidclient.BuildConfig
import mr.shtein.buddyandroidclient.retrofit.RetrofitService
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val appModule: Module = module {
    single { provideRetrofit() }
    single { provideApiService(get()) }
    single { provideFusedLocationClient(androidContext())}
    single { CancellationTokenSource() }
    single(named("userStore")) { provideUserStore(androidContext()) }
    single(named("kennelStore")) { provideKennelStore(androidContext()) }
    single(named("databaseStore")) { provideDatabaseStore(androidContext()) }
}

private fun provideRetrofit(): Retrofit =
    Retrofit.Builder()
        .baseUrl(BuildConfig.HOST)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        )
        .build()

private fun provideApiService(retrofit: Retrofit): RetrofitService =
    retrofit.create(RetrofitService::class.java)

private fun provideUserStore(context: Context) =
    SharedPreferences(context, BuildConfig.USER_STORAGE_NAME)

private fun provideKennelStore(context: Context) =
    SharedPreferences(context, BuildConfig.KENNEL_STORAGE_NAME)

private fun provideDatabaseStore(context: Context) =
    SharedPreferences(context, BuildConfig.DATABASE_STORAGE_NAME)

private fun provideFusedLocationClient(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)
