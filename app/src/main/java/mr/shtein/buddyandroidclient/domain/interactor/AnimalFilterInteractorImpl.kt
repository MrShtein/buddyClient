package mr.shtein.buddyandroidclient.domain.interactor

import mr.shtein.buddyandroidclient.data.repository.FilterPropertiesRepository
import mr.shtein.buddyandroidclient.model.dto.AnimalFilter

class AnimalFilterInteractorImpl(
    private val animalFilterPropertiesRepository: FilterPropertiesRepository
) : AnimalFilterInteractor {

    override fun makeAnimalFilter(): AnimalFilter {
        val animalTypeIdList: MutableList<Int>? = animalFilterPropertiesRepository.getAnimalTypeId()
        val cityIdList: MutableList<Int>? = animalFilterPropertiesRepository.getCityId()
        val breedIdList: MutableList<Int>? = animalFilterPropertiesRepository.getBreedId()
        val characteristicIdList: MutableList<Int>? =
            animalFilterPropertiesRepository.getCharacteristicId()
        val genderIdList: MutableList<Int>? = animalFilterPropertiesRepository.getGenderId()
        val minAge: Int = animalFilterPropertiesRepository.getMinAge()
        val maxAge: Int = animalFilterPropertiesRepository.getMaxAge()

        return AnimalFilter(
            animalTypeId = animalTypeIdList,
            cityId = cityIdList,
            breedId = breedIdList,
            characteristicId = characteristicIdList,
            genderId = genderIdList,
            minAge = minAge,
            maxAge = maxAge
        )
    }

    override fun getAnimalTypeId(): MutableList<Int>? {
       return animalFilterPropertiesRepository.getAnimalTypeId()
    }

    override fun saveAnimalTypeId(animalTypeId: MutableList<Int>) {
        animalFilterPropertiesRepository.saveAnimalTypeId(animalTypeId)
    }

    override fun saveCityId(cityId: List<Int>) {
        animalFilterPropertiesRepository.saveCityId(cityId)
    }

    override fun saveBreedId(breedId: List<Int>) {
        animalFilterPropertiesRepository.saveBreedId(breedId)
    }

    override fun saveCharacteristicId(characteristicId: List<Int>) {
        animalFilterPropertiesRepository.saveCharacteristicId(characteristicId)
    }

    override fun saveGenderId(genderId: List<Int>) {
        animalFilterPropertiesRepository.saveGenderId(genderId)
    }

    override fun saveMinAge(minAge: Int) {
        animalFilterPropertiesRepository.saveMinAge(minAge)
    }

    override fun saveMaxAge(maxAge: Int) {
        animalFilterPropertiesRepository.saveMaxAge(maxAge)
    }

    override fun removeAllData() {
        animalFilterPropertiesRepository.removeAll()
    }
}