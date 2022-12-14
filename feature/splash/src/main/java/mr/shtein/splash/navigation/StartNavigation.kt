package mr.shtein.splash.navigation

import android.os.Bundle

interface StartNavigation {
    fun moveToCityChoiceFromSplashScreen()
    fun moveToAnimalListFromSplash(animalFilter: Bundle)
}