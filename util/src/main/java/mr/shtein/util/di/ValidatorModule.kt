package mr.shtein.util.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mr.shtein.util.CommonViewModel
import mr.shtein.util.validator.PasswordValidator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val validatorModule: Module = module {
    factory { PasswordValidator(get(), provideCoroutineScope()) }
    viewModel { CommonViewModel() }
}

private fun provideCoroutineScope() = CoroutineScope(Dispatchers.Main)
