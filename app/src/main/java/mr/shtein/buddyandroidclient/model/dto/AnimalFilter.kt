package mr.shtein.buddyandroidclient.model.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnimalFilter(
    var animalTypeId: MutableList<Int>? = null,
    val cityId: List<Int>? = null,
    val breedId: List<Int>? = null,
    val characteristicId: List<Int>? = null,
    val genderId: List<Int>? = null,
    val minAge: Int = -1,
    val maxAge: Int = -1
) : Parcelable
