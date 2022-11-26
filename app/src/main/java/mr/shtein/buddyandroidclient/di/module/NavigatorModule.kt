package mr.shtein.buddyandroidclient.di.module

import mr.shtein.buddyandroidclient.navigator.Navigator
import org.koin.core.module.Module
import org.koin.dsl.module

val navigatorModule: Module = module {
    single<Navigator> { Navigator() }
}

