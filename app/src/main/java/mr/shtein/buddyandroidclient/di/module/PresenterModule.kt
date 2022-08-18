package mr.shtein.buddyandroidclient.di.module

import mr.shtein.buddyandroidclient.presentation.presenter.AnimalListPresenter
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalsListPresenterImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val presenterModule: Module = module {
    single<AnimalListPresenter> { AnimalsListPresenterImpl(get(), get(), get()) }
}

