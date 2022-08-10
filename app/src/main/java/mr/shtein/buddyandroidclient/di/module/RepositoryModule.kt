package mr.shtein.buddyandroidclient.di.module

import mr.shtein.buddyandroidclient.data.repository.*
import org.koin.core.module.Module
import org.koin.dsl.module


val repositoryModule: Module = module {
    single<AnimalRepository> { RetrofitAnimalRepository(get()) }
    single<UserRepository> { RetrofitUserRepository(get()) }
    single<LocationRepository> { RetrofitLocationRepository(get()) }
    single<UserPropertiesRepository> { SharedUserPropertiesRepository() }
    factory<KennelPropertiesRepository> { SharedKennelPropertiesRepository() }
    factory<DatabasePropertiesRepository> { SharedDatabasePropertiesRepository() }
}