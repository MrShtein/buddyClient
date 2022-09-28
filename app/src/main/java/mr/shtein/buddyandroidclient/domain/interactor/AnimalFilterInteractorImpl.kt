package mr.shtein.buddyandroidclient.domain.interactor

import mr.shtein.buddyandroidclient.data.repository.FilterPropertiesRepository
import mr.shtein.buddyandroidclient.model.dto.AnimalFilter

class AnimalFilterInteractorImpl(
    private val animalFilterPropertiesRepository: FilterPropertiesRepository
) : AnimalFilterInteractor {

    override fun makeAnimalFilter(): AnimalFilter {
        val animalTypeIdList: MutableList<Int> = animalFilterPropertiesRepository.getAnimalTypeIdList()
        val cityIdList: MutableList<Int> = animalFilterPropertiesRepository.getCityIdList()
        val breedIdList: MutableList<Int> = animalFilterPropertiesRepository.getBreedIdList()
        val characteristicIdList: MutableList<Int> =
            animalFilterPropertiesRepository.getCharacteristicIdList()
        val genderIdList: MutableList<Int> = animalFilterPropertiesRepository.getGenderIdList()
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

    override fun getAnimalTypeIdList(): MutableList<Int> {
        return animalFilterPropertiesRepository.getAnimalTypeIdList()
    }

    override fun saveAnimalTypeIdList(animalTypeId: MutableList<Int>) {
        animalFilterPropertiesRepository.saveAnimalTypeIdList(animalTypeId)
    }

    override fun getCityIdList(): MutableList<Int> {
        return animalFilterPropertiesRepository.getCityIdList()
    }

    override fun saveCityIdList(cityId: MutableList<Int>) {
        animalFilterPropertiesRepository.saveCityIdList(cityId)
    }

    override fun getBreedIdList(): MutableList<Int> {
        return animalFilterPropertiesRepository.getBreedIdList()
    }

    override fun saveBreedIdList(breedId: MutableList<Int>) {
        animalFilterPropertiesRepository.saveBreedIdList(breedId)
    }

    override fun getCharacteristicIdList(): MutableList<Int> {
        return animalFilterPropertiesRepository.getCharacteristicIdList()
    }

    override fun saveCharacteristicIdList(characteristicId: MutableList<Int>) {
        animalFilterPropertiesRepository.saveCharacteristicIdList(characteristicId)
    }

    override fun getGenderIdList(): MutableList<Int> {
        return animalFilterPropertiesRepository.getGenderIdList()
    }

    override fun saveGenderIdList(genderId: MutableList<Int>) {
        animalFilterPropertiesRepository.saveGenderIdList(genderId)
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