package mr.shtein.auth.di

import android.content.Context
import mr.shtein.auth.presentation.BottomSheetDialogShower
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val bottomSheetBuilder: Module = module {
    single { (activityContext: Context) ->
        BottomSheetDialogShower(activityContext, get())
    }
}