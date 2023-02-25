package mr.shtein.buddyandroidclient.navigation

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.animal.navigation.AnimalNavigation
import mr.shtein.auth.navigation.AuthNavigation
import mr.shtein.buddyandroidclient.R
import mr.shtein.ui_util.FragmentsListForAssigningAnimation
import mr.shtein.navigator.BaseNavigator
import mr.shtein.splash.navigation.StartNavigation
import mr.shtein.city.navigation.CityNavigation
import mr.shtein.data.model.Animal
import mr.shtein.data.model.AnimalFilter
import mr.shtein.data.model.KennelRequest
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.profile.navigation.ProfileNavigation

class Navigator() : BaseNavigator(), StartNavigation, CityNavigation, KennelNavigation,
    ProfileNavigation, AuthNavigation, BottomSheetNavigation, AnimalNavigation {

    override fun moveToCityChoiceFromSplashScreen() {
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

    override fun addTransitionsForKennelFlow(fragment: Fragment) {
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.label == KENNEL_SETTINGS_LABEL || destination.label == KENNEL_HOME_LABEL) {
                fragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            }
        }
    }

    override fun moveToKennelSettings() {
        navController?.navigate(R.id.action_addKennelFragment_to_kennelSettingsFragment)
    }

    override fun moveToKennelHome(kennel: Bundle) {
        navController?.navigate(
            R.id.action_addKennelFragment_to_kennelHomeFragment,
            kennel
        )
    }

    override fun backToPreviousFragment() {
        navController?.popBackStack()
    }

    override fun moveToAddAnimal(animal: Animal, isFromSettings: Boolean) {
        val bundle = bundleOf(
            ANIMAL_KEY to animal,
            FROM_SETTINGS_FRAGMENT_KEY to isFromSettings
        )
        navController?.navigate(
            R.id.action_animalSettingsFragment_to_addAnimalFragment,
            bundle
        )
    }

    override fun moveToAddAnimalFromKennelConfirm() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.addKennelFragment, true)
            .build()
        navController?.navigate(
            R.id.action_kennelConfirmFragment_to_addKennelFragment,
            null,
            navOptions
        )
    }

    override fun moveToAnimalSettings(animal: Animal) {
        val bundle = Bundle()
        bundle.putParcelable(ANIMAL_KEY, animal)
        navController?.navigate(
            R.id.action_kennelHomeFragment_to_animalSettingsFragment,
            bundle
        )
    }

    override fun moveToAddAnimalFromKennelHome(kennelId: Int, animalTypeId: Int) {
        val bundle = bundleOf(
            KENNEL_ID_KEY to kennelId,
            ANIMAL_TYPE_ID_KEY to animalTypeId
        )
        navController?.navigate(
            R.id.action_kennelHomeFragment_to_addAnimalFragment,
            bundle
        )
    }

    override fun moveToCityChoiceFromKennelSettings() {
        navController?.navigate(R.id.action_kennelSettingsFragment_to_cityChoiceFragment)
    }

    override fun moveToKennelConfirmFragment(kennelRequest: KennelRequest) {
        val bundle = bundleOf(SETTINGS_DATA_KEY to kennelRequest)
        navController?.navigate(R.id.action_kennelSettingsFragment_to_kennelConfirmFragment, bundle)
    }

    override fun moveToUserSettings() {
        navController?.navigate(R.id.action_userProfileFragment_to_userSettingsFragment)
    }

    override fun moveToCityChoiceFromUserProfile() {
        navController?.navigate(R.id.action_userProfileFragment_to_cityChoiceFragment)
    }

    override fun moveToCItyChoiceFromUserSettings() {
        navController?.navigate(R.id.action_userSettingsFragment_to_cityChoiceFragment)
    }

    override fun moveToResetPassword() {
        navController?.navigate(R.id.action_loginFragment_to_resetPasswordFragment)
    }

    override fun moveToAnimalListFragment(fromFragment: FragmentsListForAssigningAnimation) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.animalsListFragment, false)
            .build()
        val lastFragmentBundle = bundleOf().apply {
            putParcelable(
                LAST_FRAGMENT_KEY,
                FragmentsListForAssigningAnimation.LOGIN
            )
        }

        navController?.navigate(
            R.id.action_loginFragment_to_animalsListFragment,
            lastFragmentBundle,
            navOptions
        )
    }

    override fun moveToLogin(isFromRegistration: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(IS_FROM_REGISTRATION_KEY, isFromRegistration)
        navController?.navigate(
            R.id.action_userRegistrationFragment_to_loginFragment,
            bundle
        )
    }

    override fun moveFromAnimalListToUserProfile() {
        navController?.navigate(R.id.action_animalsListFragment_to_userProfileFragment)
    }

    override fun moveFromAddKennelToUserProfile() {
        navController?.navigate(R.id.action_addKennelFragment_to_userProfileFragment)
    }

    override fun moveFromAnimalListToAddKennel(fromFragment: FragmentsListForAssigningAnimation) {
        val lastFragmentBundle = bundleOf()
        lastFragmentBundle.putParcelable(LAST_FRAGMENT_KEY, fromFragment)
        navController?.navigate(
            R.id.action_animalsListFragment_to_addKennelFragment,
            lastFragmentBundle
        )
    }

    override fun moveFromUserProfileToAddKennel() {
        navController?.navigate(R.id.action_userProfileFragment_to_addKennelFragment)
    }

    override fun moveFromAddKennelToAnimalList(fromFragment: FragmentsListForAssigningAnimation) {
        val lastFragmentBundle = bundleOf()
        lastFragmentBundle.putParcelable(LAST_FRAGMENT_KEY, fromFragment)
        navController?.navigate(
            R.id.action_addKennelFragment_to_animalsListFragment,
            lastFragmentBundle
        )
    }

    override fun moveToVolunteerListFromKennelHome() {
        navController?.navigate(R.id.action_kennelHomeFragment_to_volunteersListFragment)
    }

    override fun moveFromUserProfileToAnimalList(fromFragment: FragmentsListForAssigningAnimation) {
        val lastFragmentBundle = bundleOf()
        lastFragmentBundle.putParcelable(LAST_FRAGMENT_KEY, fromFragment)
        navController?.navigate(
            R.id.action_userProfileFragment_to_animalsListFragment,
            lastFragmentBundle
        )
    }

    override fun moveToLoginFromAnimalList() {
        navController?.navigate(R.id.action_animalsListFragment_to_loginFragment)
    }

    override fun moveToUserRegistration() {
        navController?.navigate(R.id.action_animalsListFragment_to_userRegistrationFragment)
    }

    override fun moveToAnimalListFromAnimalFilter(from: FragmentsListForAssigningAnimation) {
        val bundle = bundleOf(LAST_FRAGMENT_KEY to from)
        navController?.navigate(R.id.action_animalFilterFragment_to_animalsListFragment, bundle)
    }

    override fun moveToAnimalCardFromAnimalList(animal: Animal) {
        val bundle = Bundle()
        bundle.putParcelable(ANIMAL_KEY, animal)
        navController?.navigate(R.id.action_animalsListFragment_to_animalsCardFragment, bundle)
    }

    override fun moveToAnimalFilterFromAnimalList(animalFilter: AnimalFilter) {
        val bundle = Bundle()
        bundle.putParcelable(ANIMAL_FILTER_KEY, animalFilter)
        navController?.navigate(R.id.action_animalsListFragment_to_animalFilterFragment, bundle)
    }

    override fun addOnDestinationChangeListener(onDestinationChangedListener: NavController.OnDestinationChangedListener) {
        navController?.addOnDestinationChangedListener(onDestinationChangedListener)
    }

    override fun removeDestinationChangeListener(onDestinationChangedListener: NavController.OnDestinationChangedListener) {
        navController?.removeOnDestinationChangedListener(onDestinationChangedListener)
    }

    companion object {
        private const val LAST_FRAGMENT_KEY = "last_fragment"
        private const val KENNEL_SETTINGS_LABEL = "KennelSettingsFragment"
        private const val KENNEL_HOME_LABEL = "KennelHomeFragment"
        private const val ANIMAL_KEY = "animal_key"
        private const val FROM_SETTINGS_FRAGMENT_KEY = "I'm from settings"
        private const val KENNEL_ID_KEY = "kennel_id"
        private const val ANIMAL_TYPE_ID_KEY = "animal_type_id"
        private const val SETTINGS_DATA_KEY = "settings_data"
        private const val IS_FROM_REGISTRATION_KEY = "is_from_registration"
        private const val ANIMAL_FILTER_KEY = "animal_filter"

    }
}