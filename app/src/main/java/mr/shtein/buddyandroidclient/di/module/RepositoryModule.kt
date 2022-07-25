package mr.shtein.buddyandroidclient.di.module

import mr.shtein.buddyandroidclient.repository.AnimalRepository
import mr.shtein.buddyandroidclient.repository.UserRepository
import org.koin.core.module.Module
import org.koin.dsl.module


val repositoryModule: Module = module {
    single<AnimalRepository> { AnimalRepository() }
    single<UserRepository> { UserRepository() }
}