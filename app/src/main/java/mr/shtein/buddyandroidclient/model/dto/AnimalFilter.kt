package mr.shtein.buddyandroidclient.model.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnimalFilter(
    var animalTypeId: MutableList<Int>? = null,
    var cityId: MutableList<Int>? = null,
    var breedId: MutableList<Int>? = null,
    var characteristicId: MutableList<Int>? = null,
    var genderId: MutableList<Int>? = null,
    var minAge: Int = -1,
    var maxAge: Int = -1
) : Parcelable
