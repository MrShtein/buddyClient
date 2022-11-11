package mr.shtein.buddyandroidclient.di.module

import mr.shtein.buddyandroidclient.data.mapper.AnimalMapper
import mr.shtein.buddyandroidclient.data.mapper.AnimalPhotoMapper
import mr.shtein.buddyandroidclient.data.mapper.CoordinatesMapper
import mr.shtein.buddyandroidclient.data.mapper.KennelMapper
import org.koin.core.module.Module
import org.koin.dsl.module

val mapperModule: Module = module {
    factory { AnimalMapper(get(), get()) }
    factory { AnimalPhotoMapper() }
    factory { CoordinatesMapper() }
    factory { KennelMapper(get()) }
}