package mr.shtein.buddyandroidclient.di.module

import mr.shtein.animal.navigation.AnimalNavigation
import mr.shtein.auth.navigation.AuthNavigation
import mr.shtein.buddyandroidclient.navigation.BottomSheetNavigation
import mr.shtein.city.navigation.CityNavigation
import mr.shtein.buddyandroidclient.navigation.Navigator
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.profile.navigation.ProfileNavigation
import mr.shtein.splash.navigation.StartNavigation
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module

val navigatorModule: Module = module {
    single { Navigator() } binds arrayOf(
        StartNavigation::class,
        CityNavigation::class,
        KennelNavigation::class,
        ProfileNavigation::class,
        AuthNavigation::class,
        BottomSheetNavigation::class,
        AnimalNavigation::class
    )
}

