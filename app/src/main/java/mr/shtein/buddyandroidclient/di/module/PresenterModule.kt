package mr.shtein.buddyandroidclient.di.module

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.GsonBuilder
import mr.shtein.buddyandroidclient.BuildConfig
import mr.shtein.buddyandroidclient.db.CityDbHelper
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalListPresenter
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalsListPresenterImpl
import mr.shtein.buddyandroidclient.retrofit.RetrofitService
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val presenterModule: Module = module {
    single<AnimalListPresenter> { AnimalsListPresenterImpl(get(), get(), get()) }
}

