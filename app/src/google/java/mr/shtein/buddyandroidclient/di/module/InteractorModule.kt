package mr.shtein.buddyandroidclient.di.module

import com.google.android.gms.tasks.CancellationTokenSource
import mr.shtein.buddyandroidclient.domain.interactor.*
import mr.shtein.splash.domain.AnimalFilterInteractor
import org.koin.core.module.Module
import org.koin.dsl.module

val interactorModule: Module = module {
    single<AnimalInteractor> { AnimalInteractorImpl(get(), get(), get(), get(), get(), get()) }
    single<AnimalFilterInteractor> { AnimalFilterInteractorImpl(get()) }
    single<LocationInteractor> { LocationServiceInteractor(get(), CancellationTokenSource()) }
}
