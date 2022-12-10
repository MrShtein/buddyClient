package mr.shtein.city.di

import mr.shtein.city.data.CityRepository
import mr.shtein.city.data.DatabasePropertiesRepository
import mr.shtein.city.data.LocalDbCityRepository
import mr.shtein.city.data.SharedDatabasePropertiesRepository
import mr.shtein.city.data.CityDbHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val DATABASE_STORE_NAME = "databaseStore"

val cityModule: Module = module {
    factory { CityDbHelper(androidContext(), get()) }
    single<CityRepository> { LocalDbCityRepository(get(), get()) }
    factory<DatabasePropertiesRepository> {
        SharedDatabasePropertiesRepository(get(named(DATABASE_STORE_NAME)))
    }
}