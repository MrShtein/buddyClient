package mr.shtein.buddyandroidclient.data.repository

interface FilterPropertiesRepository {
    fun getAnimalTypeIdList(): MutableList<Int>
    fun saveAnimalTypeIdList(animalTypeId: MutableList<Int>)

    fun getCityIdList(): MutableList<Int>
    fun saveCityIdList(cityId: List<Int>)

    fun getBreedIdList(): MutableList<Int>
    fun saveBreedIdList(breedId: List<Int>)

    fun getCharacteristicIdList(): MutableList<Int>
    fun saveCharacteristicIdList(characteristicId: List<Int>)

    fun getGenderIdList(): MutableList<Int>
    fun saveGenderIdList(genderId: List<Int>)

    fun getMinAge(): Int
    fun saveMinAge(minAge: Int)

    fun getMaxAge(): Int
    fun saveMaxAge(maxAge: Int)

    fun removeAll()

}