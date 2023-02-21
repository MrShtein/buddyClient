package mr.shtein.domain.di

import mr.shtein.domain.interactor.AnimalFilterInteractor
import mr.shtein.domain.interactor.AnimalFilterInteractorImpl
import mr.shtein.util.di.KennelAdministratorInteractor
import mr.shtein.domain.interactor.KennelAdministratorInteractorImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val commonInteractorModule: Module = module {
    single<AnimalFilterInteractor> { AnimalFilterInteractorImpl(get()) }
    single<KennelAdministratorInteractor> {
        KennelAdministratorInteractorImpl(
            get(),
            get()
        )
    }
}