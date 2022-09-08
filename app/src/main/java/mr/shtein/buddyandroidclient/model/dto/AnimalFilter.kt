package mr.shtein.buddyandroidclient.model.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnimalFilter(
    val animalTypeId: List<Int>?,
    val cityId: List<Int>?,
    val breedId: List<Int>?,
    val characteristicId: List<Int>?,
    val genderId: List<Int>?,
    val minAge: Int,
    val maxAge: Int
) : Parcelable
