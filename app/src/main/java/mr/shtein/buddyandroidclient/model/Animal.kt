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
) : Parcelable {

    fun getAge(): String {
        val years = age / 12
        val months = age % 12
        return if (years == 0) {
            "$months м."
        } else if (months == 0) {
            "$years ${getLetterForYear(years)}"
        } else {
            "$years ${getLetterForYear(years)} $months м."
        }
    }

    private fun getLetterForYear(years: Int): String {
        return when(years) {
            1,2,3,4 -> "г."
            else -> "л."
        }
    }

}
