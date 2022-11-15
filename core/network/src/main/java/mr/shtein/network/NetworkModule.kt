package mr.shtein.network

import com.google.gson.GsonBuilder
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val networkModule: Module = module {
    single { provideRetrofit() }
    single { provideNetworkService(get())}
}

fun provideRetrofit(): Retrofit =
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

fun provideNetworkService(retrofit: Retrofit): NetworkService =
     retrofit.create(NetworkService::class.java)

