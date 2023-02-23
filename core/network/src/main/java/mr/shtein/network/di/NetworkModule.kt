package mr.shtein.network.di

import com.google.gson.GsonBuilder
import mr.shtein.network.BuildConfig
import mr.shtein.network.ImageLoader
import mr.shtein.network.NetworkImageLoader
import mr.shtein.network.NetworkService
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

val networkModule: Module = module {
    single { provideRetrofit() }
    single { provideNetworkService(get()) }
    single { provideNetworkImageLoader() }
}

fun provideRetrofit(): Retrofit {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(BuildConfig.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
        .connectTimeout(BuildConfig.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
        .build()

    return Retrofit.Builder()
    .baseUrl(BuildConfig.HOST)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        )
        .client(okHttpClient)
        .build()
}


fun provideNetworkService(retrofit: Retrofit): NetworkService =
     retrofit.create(NetworkService::class.java)

fun provideNetworkImageLoader(): ImageLoader {
    val host = BuildConfig.HOST
    return NetworkImageLoader(host = host)
}

