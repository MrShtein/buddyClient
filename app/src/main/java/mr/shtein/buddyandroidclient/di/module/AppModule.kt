package mr.shtein.buddyandroidclient.di.module

import com.google.gson.GsonBuilder
import mr.shtein.buddyandroidclient.BuildConfig
import mr.shtein.buddyandroidclient.retrofit.RetrofitService
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val appModule: Module = module {
    single { provideRetrofit() }
    single { provideApiService(get())}
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
