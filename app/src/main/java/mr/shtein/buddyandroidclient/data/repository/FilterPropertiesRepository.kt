package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.dto.AnimalFilter

interface FilterPropertiesRepository {
    fun getAnimalTypeId(): MutableList<Int>?
    fun saveAnimalTypeId(animalTypeId: MutableList<Int>)

    fun getCityId(): MutableList<Int>?
    fun saveCityId(cityId: List<Int>)

    fun getBreedId(): MutableList<Int>?
    fun saveBreedId(breedId: List<Int>)

    fun getCharacteristicId(): MutableList<Int>?
    fun saveCharacteristicId(characteristicId: List<Int>)

    fun getGenderId(): MutableList<Int>?
    fun saveGenderId(genderId: List<Int>)

    fun getMinAge(): Int
    fun saveMinAge(minAge: Int)

    fun getMaxAge(): Int
    fun saveMaxAge(maxAge: Int)

    fun removeAll()

}