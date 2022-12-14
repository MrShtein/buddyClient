package mr.shtein.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Animal  (
    val id: Long,
    val imgUrl: List<AnimalPhoto>,
    val typeId: Int,
    val name: String,
    val gender: String,
    var age: Int,
    val description: String,
    val breed: String,
    val characteristics: Map<String, String>,
    val kennel: Kennel,
    var distance: String,
    var locationState: LocationState?
) : Parcelable {

    fun getAge(): String {
        val years = age / 12
        val months = age % 12
        return when {
            years == 0 -> {
                "$months м."
            }
            months == 0 -> {
                "$years ${getLetterForYear(years)}"
            }
            else -> {
                "$years ${getLetterForYear(years)} $months м."
            }
        }
    }

    private fun getLetterForYear(years: Int): String {
        return when(years) {
            1,2,3,4 -> "г."
            else -> "л."
        }
    }

    fun getImgUrls(): List<String> {
        val photoList = mutableListOf<String>()
        imgUrl.map {
            photoList.add(it.url)
        }
        return photoList
    }
}

@Parcelize
enum class LocationState : Parcelable {
    INIT_STATE,
    SEARCH_STATE,
    DISTANCE_VISIBLE_STATE,
    BAD_RESULT_STATE
}



