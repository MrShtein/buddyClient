package mr.shtein.buddyandroidclient.di.module

import mr.shtein.city.navigation.CityNavigation
import mr.shtein.buddyandroidclient.navigator.Navigator
import mr.shtein.splash.navigation.StartNavigation
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module

val navigatorModule: Module = module {
    single { Navigator() } binds arrayOf(StartNavigation::class, mr.shtein.city.navigation.CityNavigation::class)
}

