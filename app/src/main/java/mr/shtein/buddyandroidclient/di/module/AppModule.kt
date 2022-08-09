package mr.shtein.buddyandroidclient.di.module

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import mr.shtein.buddyandroidclient.BuildConfig
import mr.shtein.buddyandroidclient.retrofit.RetrofitService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val appModule: Module = module {
    single { provideRetrofit() }
    single { provideApiService(get())}
    single<SharedPreferences>(named("userStore")) { provideUserStore(androidContext()) }
    single<SharedPreferences>(named("kennelStore")) { provideKennelStore(androidContext()) }
    single<SharedPreferences>(named("databaseStore")) { provideDatabaseStore(androidContext()) }
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
    context.getSharedPreferences(BuildConfig.USER_STORAGE_NAME, Context.MODE_PRIVATE)

private fun provideKennelStore(context: Context) =
    context.getSharedPreferences(BuildConfig.KENNEL_STORAGE_NAME, Context.MODE_PRIVATE)

private fun provideDatabaseStore(context: Context) =
    context.getSharedPreferences(BuildConfig.DATABASE_STORAGE_NAME, Context.MODE_PRIVATE)