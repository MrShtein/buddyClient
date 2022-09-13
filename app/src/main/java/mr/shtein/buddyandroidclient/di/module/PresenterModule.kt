package mr.shtein.buddyandroidclient.di.module

import mr.shtein.buddyandroidclient.presentation.presenter.AnimalFilterPresenter
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalListPresenter
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalsListPresenterImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val presenterModule: Module = module {
    factory<AnimalsListPresenterImpl> { AnimalsListPresenterImpl(get(), get(), get(), get()) }
    factory<AnimalFilterPresenter> { AnimalFilterPresenter() }
}

