package mr.shtein.data.di

import android.content.Context
import mr.shtein.data.BuildConfig
import mr.shtein.data.util.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sharedPreferencesModule: Module = module {
    single(named("userStore")) { provideUserStore(androidContext()) }
    single(named("kennelStore")) { provideKennelStore(androidContext()) }
    single(named("databaseStore")) { provideDatabaseStore(androidContext()) }
    single(named("filterStore")) { provideFilterStore(androidContext()) }
    single(named("appStore")) { provideAppStore(androidContext()) }
}

private fun provideUserStore(context: Context) =
    SharedPreferences(context, BuildConfig.USER_STORAGE_NAME)

private fun provideKennelStore(context: Context) =
    SharedPreferences(context, BuildConfig.KENNEL_STORAGE_NAME)

private fun provideDatabaseStore(context: Context) =
    SharedPreferences(context, BuildConfig.DATABASE_STORAGE_NAME)

private fun provideFilterStore(context: Context) =
    SharedPreferences(context, BuildConfig.FILTER_STORAGE_NAME)

private fun provideAppStore(context: Context) =
    SharedPreferences(context, BuildConfig.APP_STORAGE_NAME)