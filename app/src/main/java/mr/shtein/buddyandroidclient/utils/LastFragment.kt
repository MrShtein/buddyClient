package mr.shtein.buddyandroidclient.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class LastFragment : Parcelable {
    ADD_KENNEL,
    ANIMAL_LIST,
    USER_PROFILE
}