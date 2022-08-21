package mr.shtein.buddyandroidclient.di.module

import com.huawei.hms.location.LocationRequest
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractor
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractorImpl
import mr.shtein.buddyandroidclient.domain.interactor.LocationInteractor
import mr.shtein.buddyandroidclient.domain.interactor.LocationServiceInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val interactorModule: Module = module {
    single<AnimalInteractor> { AnimalInteractorImpl(get(), get()) }
    single<LocationInteractor> { LocationServiceInteractor(get(), get()) }

}




