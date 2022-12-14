package mr.shtein.buddyandroidclient.navigation

import mr.shtein.ui_util.FragmentsListForAssigningAnimation

interface BottomSheetNavigation {
    fun moveFromAnimalListToUserProfile()
    fun moveFromAnimalListToAddKennel(fromFragment: FragmentsListForAssigningAnimation)

    fun moveFromAddKennelToUserProfile()
    fun moveFromAddKennelToAnimalList(fromFragment: FragmentsListForAssigningAnimation)

    fun moveFromUserProfileToAddKennel()
    fun moveFromUserProfileToAnimalList(fromFragment: FragmentsListForAssigningAnimation)
}