package mr.shtein.buddyandroidclient.domain.interactor

import mr.shtein.buddyandroidclient.model.dto.AnimalFilter

interface AnimalFilterInteractor {

    fun saveAnimalTypeId(animalTypeId: List<Int>)
    fun saveCityId(cityId: List<Int>)
    fun saveBreedId(breedId: List<Int>)
    fun saveCharacteristicId(characteristicId: List<Int>)
    fun saveGenderId(genderId: List<Int>)
    fun saveMinAge(minAge: Int)
    fun saveMaxAge(maxAge: Int)
    fun makeAnimalFilter(): AnimalFilter
    fun removeAllData(): Unit

}