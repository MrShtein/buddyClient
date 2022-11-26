package mr.shtein.buddyandroidclient.navigator

import android.os.Bundle

interface StartNavigation {
    fun moveToCityNav()
    fun moveToAnimalListFromSplash(animalFilter: Bundle)
}