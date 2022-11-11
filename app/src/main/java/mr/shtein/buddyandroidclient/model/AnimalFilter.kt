package mr.shtein.buddyandroidclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnimalFilter(
    var animalTypeId: MutableList<Int>? = null,
    var cityId: MutableList<Int>? = null,
    var breedId: MutableList<Int>? = null,
    var colorId: MutableList<Int>? = null,
    var genderId: Int = -1,
    var minAge: Int = -1,
    var maxAge: Int = -1
) : Parcelable
