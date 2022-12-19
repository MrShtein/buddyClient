package mr.shtein.domain.interactor

import mr.shtein.data.model.AnimalFilter
import mr.shtein.data.repository.FilterPropertiesRepository

class AnimalFilterInteractorImpl(
    private val animalFilterPropertiesRepository: FilterPropertiesRepository
) : AnimalFilterInteractor {

    override fun makeAnimalFilter(): AnimalFilter {
        val animalTypeIdList: MutableSet<Int> = animalFilterPropertiesRepository.getAnimalTypeIdList()
        val cityIdList: MutableSet<Int> = animalFilterPropertiesRepository.getCityIdList()
        val breedIdList: MutableSet<Int> = animalFilterPropertiesRepository.getBreedIdList()
        val characteristicIdList: MutableSet<Int> =
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
        ) //TODO Заменить на MutableList на MutableSet
    }

    override fun getAnimalTypeIdList(): MutableSet<Int> {
        return animalFilterPropertiesRepository.getAnimalTypeIdList()
    }

    override fun saveAnimalTypeIdList(animalTypeId: MutableSet<Int>) {
        animalFilterPropertiesRepository.saveAnimalTypeIdList(animalTypeId)
    }

    override fun getCityIdList(): MutableSet<Int> {
        return animalFilterPropertiesRepository.getCityIdList()
    }

    override fun saveCityIdList(cityId: MutableSet<Int>) {
        animalFilterPropertiesRepository.saveCityIdList(cityId)
    }

    override fun getBreedIdSet(): MutableSet<Int> {
        return animalFilterPropertiesRepository.getBreedIdList()
    }

    override fun saveBreedIdSet(breedId: MutableSet<Int>) {
        animalFilterPropertiesRepository.saveBreedIdList(breedId)
    }

    override fun getColorIdIdList(): MutableSet<Int> {
        return animalFilterPropertiesRepository.getColorIdList()
    }

    override fun saveColorIdList(colorIdList: MutableSet<Int>) {
        animalFilterPropertiesRepository.saveColorIdList(colorIdList)
    }

    override fun getGenderId(): Int {
        return animalFilterPropertiesRepository.getGenderId()
    }

    override fun saveGenderId(genderId: Int) {
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