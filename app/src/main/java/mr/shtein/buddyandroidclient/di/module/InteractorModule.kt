package mr.shtein.buddyandroidclient.di.module

import android.content.Context
import com.google.gson.GsonBuilder
import mr.shtein.buddyandroidclient.BuildConfig
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractor
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractorImpl
import mr.shtein.buddyandroidclient.domain.interactor.LocationInteractor
import mr.shtein.buddyandroidclient.domain.interactor.LocationServiceInteractor
import mr.shtein.buddyandroidclient.retrofit.RetrofitService
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val interactorModule: Module = module {
    single<AnimalInteractor> { AnimalInteractorImpl() }
    single<LocationInteractor> { LocationServiceInteractor() }
}
