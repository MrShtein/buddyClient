package mr.shtein.domain.interactor

import mr.shtein.data.model.AnimalFilter

interface AnimalFilterInteractor {

    fun getAnimalTypeIdList(): MutableList<Int>
    fun saveAnimalTypeIdList(animalTypeId: MutableList<Int>)

    fun getCityIdList(): MutableList<Int>
    fun saveCityIdList(cityId: MutableList<Int>)

    fun getBreedIdList(): MutableList<Int>
    fun saveBreedIdList(breedId: MutableList<Int>)

    fun getColorIdIdList(): MutableList<Int>
    fun saveColorIdList(colorIdList: MutableList<Int>)

    fun getGenderId(): Int
    fun saveGenderId(genderId: Int)

    fun saveMinAge(minAge: Int)
    fun getMinAge(): Int

    fun saveMaxAge(maxAge: Int)
    fun getMaxAge(): Int

    fun makeAnimalFilter(): AnimalFilter
    fun removeAllData(): Unit

}