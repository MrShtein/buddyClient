package mr.shtein.buddyandroidclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kennel(
    val id: Int,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val email: String,
    val avatarUrl: String,
    val coordinates: Coordinates,
) : Parcelable
