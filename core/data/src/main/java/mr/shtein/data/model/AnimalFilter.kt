package mr.shtein.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnimalFilter(
    var animalTypeId: MutableSet<Int>? = null,
    var cityId: MutableSet<Int>? = null,
    var breedId: MutableSet<Int>? = null,
    var colorId: MutableSet<Int>? = null,
    var genderId: Int = -1,
    var minAge: Int = -1,
    var maxAge: Int = -1
) : Parcelable
