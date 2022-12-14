package mr.shtein.data.repository

interface FilterPropertiesRepository {
    fun getAnimalTypeIdList(): MutableList<Int>
    fun saveAnimalTypeIdList(animalTypeId: MutableList<Int>)

    fun getCityIdList(): MutableList<Int>
    fun saveCityIdList(cityId: List<Int>)

    fun getBreedIdList(): MutableList<Int>
    fun saveBreedIdList(breedId: List<Int>)

    fun getColorIdList(): MutableList<Int>
    fun saveColorIdList(colorId: List<Int>)

    fun getGenderId(): Int
    fun saveGenderIdList(genderId: Int)

    fun getMinAge(): Int
    fun saveMinAge(minAge: Int)

    fun getMaxAge(): Int
    fun saveMaxAge(maxAge: Int)

    fun removeAll()

}