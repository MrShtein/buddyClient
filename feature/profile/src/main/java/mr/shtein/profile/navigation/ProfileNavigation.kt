package mr.shtein.profile.navigation

interface ProfileNavigation {
    fun moveToUserSettings()
    fun moveToCityChoiceFromUserProfile()
    fun moveToCItyChoiceFromUserSettings()
    fun backToPreviousFragment()
}