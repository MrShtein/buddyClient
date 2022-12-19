package mr.shtein.data.repository

interface FilterPropertiesRepository {
    fun getAnimalTypeIdList(): MutableSet<Int>
    fun saveAnimalTypeIdList(animalTypeId: MutableSet<Int>)

    fun getCityIdList(): MutableSet<Int>
    fun saveCityIdList(cityId: MutableSet<Int>)

    fun getBreedIdList(): MutableSet<Int>
    fun saveBreedIdList(breedId: MutableSet<Int>)

    fun getColorIdList(): MutableSet<Int>
    fun saveColorIdList(colorId: MutableSet<Int>)

    fun getGenderId(): Int
    fun saveGenderIdList(genderId: Int)

    fun getMinAge(): Int
    fun saveMinAge(minAge: Int)

    fun getMaxAge(): Int
    fun saveMaxAge(maxAge: Int)

    fun removeAll()

}