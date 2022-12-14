package mr.shtein.animal.di

import mr.shtein.animal.presentation.presenter.AnimalFilterPresenter
import mr.shtein.animal.presentation.presenter.AnimalListPresenter
import mr.shtein.animal.presentation.presenter.AnimalsListPresenterImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val presenterModule: Module = module {
    factory<AnimalsListPresenterImpl> { AnimalsListPresenterImpl( get(), get(), get(), get()) }
    factory<AnimalFilterPresenter> { AnimalFilterPresenter(get(), get(), get(), get()) }
}

