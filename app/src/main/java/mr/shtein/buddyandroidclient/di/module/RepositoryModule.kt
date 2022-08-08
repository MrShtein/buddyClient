package mr.shtein.buddyandroidclient.di.module

import mr.shtein.buddyandroidclient.data.repository.AnimalRepository
import mr.shtein.buddyandroidclient.data.repository.LocationRepositoryImpl
import mr.shtein.buddyandroidclient.data.repository.UserRepository
import org.koin.core.module.Module
import org.koin.dsl.module


val repositoryModule: Module = module {
    single<AnimalRepository> { AnimalRepository() }
    single<UserRepository> { UserRepository() }
    single<LocationRepositoryImpl> { LocationRepositoryImpl() }
}