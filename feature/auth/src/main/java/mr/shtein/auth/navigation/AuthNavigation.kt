package mr.shtein.auth.navigation

import mr.shtein.ui_util.FragmentsListForAssigningAnimation

interface AuthNavigation {
    fun backToPreviousFragment()
    fun moveToResetPassword()
    fun moveToAnimalListFragment(fromFragment: FragmentsListForAssigningAnimation)
    fun moveToLogin(isFromRegistration: Boolean)
}