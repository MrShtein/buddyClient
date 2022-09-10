package mr.shtein.buddyandroidclient.domain.interactor

import mr.shtein.buddyandroidclient.model.dto.AnimalFilter

interface AnimalFilterInteractor {

    fun getAnimalTypeId(): MutableList<Int>?
    fun saveAnimalTypeId(animalTypeId: MutableList<Int>)
    fun saveCityId(cityId: List<Int>)
    fun saveBreedId(breedId: List<Int>)
    fun saveCharacteristicId(characteristicId: List<Int>)
    fun saveGenderId(genderId: List<Int>)
    fun saveMinAge(minAge: Int)
    fun saveMaxAge(maxAge: Int)
    fun makeAnimalFilter(): AnimalFilter
    fun removeAllData(): Unit

}