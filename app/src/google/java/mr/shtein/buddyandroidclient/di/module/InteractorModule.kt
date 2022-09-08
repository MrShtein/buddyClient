package mr.shtein.buddyandroidclient.di.module

import com.google.android.gms.tasks.CancellationTokenSource
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractor
import mr.shtein.buddyandroidclient.domain.interactor.AnimalFilterInteractorImpl
import mr.shtein.buddyandroidclient.domain.interactor.LocationInteractor
import mr.shtein.buddyandroidclient.domain.interactor.LocationServiceInteractor
import org.koin.core.module.Module
import org.koin.dsl.module

val interactorModule: Module = module {
    single<AnimalInteractor> { AnimalFilterInteractorImpl(get(), get()) }
    single<LocationInteractor> { LocationServiceInteractor(get(), CancellationTokenSource()) }
}
