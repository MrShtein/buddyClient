package mr.shtein.buddyandroidclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnimalPhoto(
    val url: String,
    val primary: Boolean,
    val animalId: Long
) : Parcelable
