package mr.shtein.kennel.di

import kotlinx.coroutines.Dispatchers
import mr.shtein.kennel.domain.KennelInteractor
import mr.shtein.kennel.domain.KennelInteractorImpl
import mr.shtein.kennel.presentation.viewmodel.AddKennelViewModel
import mr.shtein.kennel.presentation.viewmodel.KennelSettingsViewModel
import mr.shtein.kennel.util.mapper.KennelPreviewMapper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val kennelModule: Module = module {
    single<KennelInteractor> { KennelInteractorImpl(get(), get(), kennelPreviewMapperBuilder()) }
    viewModel { AddKennelViewModel(dispatcherMain, get(), get()) }
    viewModel { KennelSettingsViewModel(get()) }
}

val dispatcherMain = Dispatchers.Main

fun kennelPreviewMapperBuilder(): KennelPreviewMapper {
    return KennelPreviewMapper()
}
