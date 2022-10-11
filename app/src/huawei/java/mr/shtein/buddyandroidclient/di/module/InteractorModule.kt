package mr.shtein.buddyandroidclient.di.module

import com.huawei.hms.location.LocationRequest
import mr.shtein.buddyandroidclient.domain.interactor.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val interactorModule: Module = module {
    single<AnimalInteractor> { AnimalInteractorImpl(get(), get(), get(), get(), get(), get()) }
    single<AnimalFilterInteractor> { AnimalFilterInteractorImpl(get()) }
    single<LocationInteractor> { LocationServiceInteractor(get(), get()) }

}




