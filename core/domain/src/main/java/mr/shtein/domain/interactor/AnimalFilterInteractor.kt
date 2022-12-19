package mr.shtein.domain.interactor

import mr.shtein.data.model.AnimalFilter

interface AnimalFilterInteractor {

    fun getAnimalTypeIdList(): MutableSet<Int>
    fun saveAnimalTypeIdList(animalTypeId: MutableSet<Int>)

    fun getCityIdList(): MutableSet<Int>
    fun saveCityIdList(cityId: MutableSet<Int>)

    fun getBreedIdSet(): MutableSet<Int>
    fun saveBreedIdSet(breedId: MutableSet<Int>)

    fun getColorIdIdList(): MutableSet<Int>
    fun saveColorIdList(colorIdList: MutableSet<Int>)

    fun getGenderId(): Int
    fun saveGenderId(genderId: Int)

    fun saveMinAge(minAge: Int)
    fun getMinAge(): Int

    fun saveMaxAge(maxAge: Int)
    fun getMaxAge(): Int

    fun makeAnimalFilter(): AnimalFilter
    fun removeAllData(): Unit

}