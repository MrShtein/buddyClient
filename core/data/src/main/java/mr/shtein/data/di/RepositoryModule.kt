package mr.shtein.data.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import mr.shtein.data.repository.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val KENNEL_STORE_NAME = "kennelStore"
const val USER_STORE_NAME = "userStore"
const val FILTER_STORE_NAME = "filterStore"
const val APP_STORE_NAME = "appStore"

val repositoryModule: Module = module {
    single<AnimalRepository> { NetworkAnimalRepository(get(), get()) }
    single<AnimalTypeRepository> { NetworkAnimalTypeRepository(get()) }
    single<AnimalBreedRepository> { NetworkAnimalBreedRepository(get()) }
    single<AnimalCharacteristicsRepository> { NetworkAnimalCharacteristicsRepository(get()) }
    single<UserRepository> { NetworkUserRepository(get()) }
    single<DistanceCounterRepository> { NetworkDistanceCounterRepository(get()) }
    single<FirebaseRepository> {
        FirebaseRepositoryImpl(
            dispatcher = getDispatcherIO(),
            firebaseMessaging = getFirebaseMessaging(),
            firebaseCrashlytics = getFirebaseCrashlytics()
        )
    }

    single<KennelRepository> { NetworkKennelRepository(get(), androidContext()) }
    single<UserPropertiesRepository> {
        SharedUserPropertiesRepository(get(named(USER_STORE_NAME)))
    }
    factory<KennelPropertiesRepository> {
        SharedKennelPropertiesRepository(get(named(KENNEL_STORE_NAME)))
    }
    factory<FilterPropertiesRepository> {
        SharedFilterPropertiesRepository(get(named(FILTER_STORE_NAME)))
    }
    factory<AppPropertiesRepository> {
        SharedAppPropertiesRepository(get(named(APP_STORE_NAME)))
    }
}

private fun getFirebaseMessaging() = FirebaseMessaging.getInstance()
private fun getFirebaseCrashlytics() = FirebaseCrashlytics.getInstance()
private fun getDispatcherIO() = Dispatchers.IO
