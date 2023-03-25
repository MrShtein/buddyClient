package mr.shtein.kennel.di

import kotlinx.coroutines.Dispatchers
import mr.shtein.data.mapper.AnimalMapper
import mr.shtein.data.mapper.AnimalPhotoMapper
import mr.shtein.data.mapper.CoordinatesMapper
import mr.shtein.data.mapper.KennelMapper
import mr.shtein.kennel.domain.KennelInteractor
import mr.shtein.kennel.domain.KennelInteractorImpl
import mr.shtein.kennel.presentation.viewmodel.*
import mr.shtein.kennel.util.mapper.KennelPreviewMapper
import mr.shtein.util.validator.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val EMAIL_VALIDATOR_KEY = "email_key"
private const val EMPTY_FIELD_VALIDATOR_KEY = "empty_field_key"
private const val PHONE_NUMBER_VALIDATOR_KEY = "phone_number_key"
private const val IDENTIFICATION_NUMBER_VALIDATOR_KEY = "identification_number_key"

val kennelModule: Module = module {
    single<KennelInteractor> {
        KennelInteractorImpl(
            get(),
            get(),
            get(),
            animalMapperBuilder(),
            kennelPreviewMapperBuilder(),
            get(qualifier = named(EMAIL_VALIDATOR_KEY)),
            get(qualifier = named(EMPTY_FIELD_VALIDATOR_KEY)),
            get(qualifier = named(PHONE_NUMBER_VALIDATOR_KEY)),
            get(qualifier = named(IDENTIFICATION_NUMBER_VALIDATOR_KEY)),
            dispatcherIO
        )
    }

    single<Validator>(named(EMAIL_VALIDATOR_KEY)) { EmailNameValidator() }
    single<Validator>(named(EMPTY_FIELD_VALIDATOR_KEY)) { EmptyFieldValidator() }
    single<Validator>(named(PHONE_NUMBER_VALIDATOR_KEY)) { PhoneNumberLengthValidator() }
    single<Validator>(named(IDENTIFICATION_NUMBER_VALIDATOR_KEY)) { IdentificationNumberValidator() }

    viewModel { AddKennelViewModel(dispatcherMain, get(), get()) }
    viewModel { KennelSettingsViewModel(get(), get()) }
    viewModel { kennelRequest ->
        KennelConfirmViewModel(kennelRequest = kennelRequest.get(), get(), get(), get(), get())
    }
    viewModel {kennelPreview ->
        KennelHomeViewModel(kennelPreview = kennelPreview.get(), get(), get())
    }
    viewModel {kennelPreview ->
        VolunteerListViewModel(get(), kennelPreview = kennelPreview.get(), get())
    }
    viewModel { animal->
        AnimalSettingsViewModel(animalId = animal.get(),get(),get(), get(), get(),get())
    }
}

val dispatcherMain = Dispatchers.Main
val dispatcherIO = Dispatchers.IO

fun kennelPreviewMapperBuilder(): KennelPreviewMapper {
    return KennelPreviewMapper()
}

fun animalMapperBuilder(): AnimalMapper {
    return AnimalMapper(
        animalPhotoMapper =  AnimalPhotoMapper(),
        kennelMapper = KennelMapper(CoordinatesMapper())
    )
}
