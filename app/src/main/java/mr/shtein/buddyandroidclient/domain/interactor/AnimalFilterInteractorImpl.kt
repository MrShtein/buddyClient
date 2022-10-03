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
            animalFilterPropertiesRepository.getColorIdList()
        val genderId: Int = animalFilterPropertiesRepository.getGenderId()
        val minAge: Int = animalFilterPropertiesRepository.getMinAge()
        val maxAge: Int = animalFilterPropertiesRepository.getMaxAge()

        return AnimalFilter(
            animalTypeId = animalTypeIdList,
            cityId = cityIdList,
            breedId = breedIdList,
            colorId = characteristicIdList,
            genderId = genderId,
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

    override fun getColorIdIdList(): MutableList<Int> {
        return animalFilterPropertiesRepository.getColorIdList()
    }

    override fun saveColorIdList(colorIdList: MutableList<Int>) {
        animalFilterPropertiesRepository.saveColorIdList(colorIdList)
    }

    override fun getGenderId(): Int {
        return animalFilterPropertiesRepository.getGenderId()
    }

    override fun saveGenderIdList(genderId: Int) {
        animalFilterPropertiesRepository.saveGenderIdList(genderId)
    }

    override fun saveMinAge(minAge: Int) {
        animalFilterPropertiesRepository.saveMinAge(minAge)
    }

    override fun getMinAge(): Int {
        return animalFilterPropertiesRepository.getMinAge()
    }

    override fun saveMaxAge(maxAge: Int) {
        animalFilterPropertiesRepository.saveMaxAge(maxAge)
    }

    override fun getMaxAge(): Int {
        return animalFilterPropertiesRepository.getMaxAge()
    }

    override fun removeAllData() {
        animalFilterPropertiesRepository.removeAll()
    }
}