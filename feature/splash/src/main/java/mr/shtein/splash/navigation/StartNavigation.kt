package mr.shtein.splash.navigation

import android.os.Bundle

interface StartNavigation {
    fun moveToCityNav()
    fun moveToAnimalListFromSplash(animalFilter: Bundle)
}