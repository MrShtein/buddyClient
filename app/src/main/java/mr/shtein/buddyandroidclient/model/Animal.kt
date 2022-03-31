package mr.shtein.buddyandroidclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Animal  (
    val id: Long,
    val imgUrl: List<AnimalPhoto>,
    val spices: String,
    val name: String,
    val kennelName: String,
    val gender: String,
    val age: Int,
    val description: String,
    val breed: String,
    val characteristics: Map<String, String>
) : Parcelable
