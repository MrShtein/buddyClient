package mr.shtein.buddyandroidclient

import android.app.Application
import mr.shtein.buddyandroidclient.di.module.repositoryModule
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalListPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BuddyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BuddyApplication)
            modules(repositoryModule)
        }
    }
}