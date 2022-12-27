package mr.shtein.kennel.di

import kotlinx.coroutines.Dispatchers
import mr.shtein.kennel.domain.KennelInteractor
import mr.shtein.kennel.domain.KennelInteractorImpl
import mr.shtein.kennel.presentation.viewmodel.AddKennelViewModel
import mr.shtein.kennel.presentation.viewmodel.KennelSettingsViewModel
import mr.shtein.kennel.util.mapper.KennelPreviewMapper
import mr.shtein.util.validator.EmailNameValidator
import mr.shtein.util.validator.EmptyFieldValidator
import mr.shtein.util.validator.PhoneNumberLengthValidator
import mr.shtein.util.validator.Validator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val EMAIL_VALIDATOR_KEY = "email_key"
private const val EMPTY_FIELD_VALIDATOR_KEY = "empty_field_key"
private const val PHONE_NUMBER_VALIDATOR_KEY = "phone_number_key"

val kennelModule: Module = module {
    single<KennelInteractor> {
        KennelInteractorImpl(
            get(),
            get(),
            kennelPreviewMapperBuilder(),
            get(qualifier = named(EMAIL_VALIDATOR_KEY)),
            get(qualifier = named(EMPTY_FIELD_VALIDATOR_KEY)),
            get(qualifier = named(PHONE_NUMBER_VALIDATOR_KEY)),
            dispatcherIO
        )
    }

    single<Validator>(named(EMAIL_VALIDATOR_KEY)) { EmailNameValidator() }
    single<Validator>(named(EMPTY_FIELD_VALIDATOR_KEY)) { EmptyFieldValidator() }
    single<Validator>(named(PHONE_NUMBER_VALIDATOR_KEY)) { PhoneNumberLengthValidator() }



    viewModel { AddKennelViewModel(dispatcherMain, get(), get()) }
    viewModel { KennelSettingsViewModel(get(), get()) }
}

val dispatcherMain = Dispatchers.Main
val dispatcherIO = Dispatchers.IO

fun kennelPreviewMapperBuilder(): KennelPreviewMapper {
    return KennelPreviewMapper()
}
