package mr.shtein.buddyandroidclient

import android.app.Application
import mr.shtein.buddyandroidclient.di.module.*
import mr.shtein.city.di.cityModule
import mr.shtein.data.di.mapperModule
import mr.shtein.data.di.repositoryModule
import mr.shtein.data.di.sharedPreferencesModule
import mr.shtein.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BuddyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BuddyApplication)
            modules(
                listOf(
                    appModule,
                    networkModule,
                    repositoryModule,
                    interactorModule,
                    presenterModule,
                    mapperModule,
                    navigatorModule,
                    sharedPreferencesModule,
                    cityModule
                )
            )
        }
    }
}