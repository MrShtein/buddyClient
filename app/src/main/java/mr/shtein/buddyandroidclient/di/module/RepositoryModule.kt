package mr.shtein.buddyandroidclient.di.module

import mr.shtein.buddyandroidclient.data.repository.*
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val DATABASE_STORE_NAME = "databaseStore"
const val KENNEL_STORE_NAME = "kennelStore"
const val USER_STORE_NAME = "userStore"
const val FILTER_STORE_NAME = "filterStore"

val repositoryModule: Module = module {
    single<AnimalRepository> { NetworkAnimalRepository(get()) }
    single<AnimalTypeRepository> { NetworkAnimalTypeRepository(get()) }
    single<AnimalBreedRepository> { NetworkAnimalBreedRepository(get()) }
    single<AnimalCharacteristicsRepository> { NetworkAnimalCharacteristicsRepository(get()) }
    single<UserRepository> { NetworkUserRepository(get()) }
    single<DistanceCounterRepository> { NetworkDistanceCounterRepository(get()) }
    single<CityRepository> { LocalDbCityRepository(get(), get()) }
    single<UserPropertiesRepository> {
        SharedUserPropertiesRepository(get(named(USER_STORE_NAME)))
    }
    factory<KennelPropertiesRepository> {
        SharedKennelPropertiesRepository(get(named(KENNEL_STORE_NAME)))
    }
    factory<DatabasePropertiesRepository> {
        SharedDatabasePropertiesRepository(get(named(DATABASE_STORE_NAME)))
    }
    factory<FilterPropertiesRepository> {
        SharedFilterPropertiesRepository(get(named(FILTER_STORE_NAME)))
    }
}