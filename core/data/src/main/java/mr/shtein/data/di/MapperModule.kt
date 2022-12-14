package mr.shtein.data.di

import mr.shtein.data.mapper.AnimalMapper
import mr.shtein.data.mapper.AnimalPhotoMapper
import mr.shtein.data.mapper.CoordinatesMapper
import mr.shtein.data.mapper.KennelMapper
import org.koin.core.module.Module
import org.koin.dsl.module

val mapperModule: Module = module {
    factory { AnimalMapper(get(), get()) }
    factory { AnimalPhotoMapper() }
    factory { CoordinatesMapper() }
    factory { KennelMapper(get()) }
}