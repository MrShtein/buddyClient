package mr.shtein.buddyandroidclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class KennelPreview(
    val kennelId: Int,
    val volunteersAmount: Int,
    val animalsAmount: Int,
    val name: String,
    val avatarUrl: String,
    val isValid: Boolean
): Parcelable
