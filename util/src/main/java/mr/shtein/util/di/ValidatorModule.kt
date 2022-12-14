package mr.shtein.util.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mr.shtein.util.validator.PasswordValidator
import org.koin.core.module.Module
import org.koin.dsl.module

val validatorModule: Module = module {
    factory { PasswordValidator(get(), provideCoroutineScope()) }
}

private fun provideCoroutineScope() = CoroutineScope(Dispatchers.Main)
