package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.model.dto.AnimalFilter

interface FilterPropertiesRepository {
    fun getAnimalTypeId(): List<Int>?
    fun saveAnimalTypeId(animalTypeId: List<Int>)

    fun getCityId(): List<Int>?
    fun saveCityId(cityId: List<Int>)

    fun getBreedId(): List<Int>?
    fun saveBreedId(breedId: List<Int>)

    fun getCharacteristicId(): List<Int>?
    fun saveCharacteristicId(characteristicId: List<Int>)

    fun getGenderId(): List<Int>?
    fun saveGenderId(genderId: List<Int>)

    fun getMinAge(): Int
    fun saveMinAge(minAge: Int)

    fun getMaxAge(): Int
    fun saveMaxAge(maxAge: Int)

    fun removeAll()

}