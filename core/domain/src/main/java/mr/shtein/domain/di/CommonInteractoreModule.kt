package mr.shtein.domain.di

import mr.shtein.domain.interactor.AnimalFilterInteractor
import mr.shtein.domain.interactor.AnimalFilterInteractorImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val commonInteractorModule: Module = module {
    single<AnimalFilterInteractor> { AnimalFilterInteractorImpl(get()) }
}