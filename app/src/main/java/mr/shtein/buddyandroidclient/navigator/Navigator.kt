package mr.shtein.buddyandroidclient.navigator

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.utils.FragmentsListForAssigningAnimation
import mr.shtein.navigator.BaseNavigator
import mr.shtein.splash.navigation.StartNavigation
import mr.shtein.city.navigation.CityNavigation

class Navigator() : BaseNavigator(), StartNavigation, CityNavigation {

    override fun moveToCityNav() {
        navController?.navigate(R.id.action_startFragment_to_cityChoiceFragment)
    }
    override fun moveToAnimalListFromSplash(animalFilter: Bundle) {
        navController?.navigate(
            R.id.action_startFragment_to_animalsListFragment, animalFilter
        )
    }

    override fun getPreviousBackStackEntry(): String? {
        return navController?.previousBackStackEntry?.id
    }

    override fun getPreviousNavLabel(): CharSequence? {
        return navController?.previousBackStackEntry?.destination?.label
    }

    override fun popBackStack() {
        navController?.popBackStack()
    }

    override fun backToAnimalList() {
        navController?.popBackStack(destinationId = R.id.animalsListFragment, inclusive = false)
    }

    override fun moveToAnimalListFromCity() {
        val bundle = bundleOf(
            LAST_FRAGMENT_KEY to FragmentsListForAssigningAnimation.CITY_CHOICE
        )
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.cityChoiceFragment, true)
            .build()
        navController?.navigate(
            R.id.action_cityChoiceFragment_to_animalsListFragment,
            bundle,
            navOptions
        )
    }

    companion object {
        private const val LAST_FRAGMENT_KEY = "last_fragment"
    }
}