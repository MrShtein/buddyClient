package mr.shtein.ui_util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class FragmentsListForAssigningAnimation : Parcelable {
    ADD_KENNEL,
    ANIMAL_LIST,
    USER_PROFILE,
    ANIMAL_CARD,
    REGISTRATION,
    LOGIN,
    START,
    CITY_CHOICE,
    ANIMAL_FILTER,
    OTHER
}