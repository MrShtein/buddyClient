package mr.shtein.data.repository

import mr.shtein.data.util.SharedPreferences


const val ANIMAL_TYPE_KEY = "animal_type"
const val CITY_KEY = "city"
const val BREED_KEY = "breed"
const val COLOR_KEY = "color"
private const val GENDER_KEY = "gender"
private const val MIN_AGE_KEY = "min_age"
private const val MAX_AGE_KEY = "max_age"
const val NO_AGE_FILTER_VALUE = -1

class SharedFilterPropertiesRepository(private val storage: SharedPreferences) :
    FilterPropertiesRepository {
    override fun getAnimalTypeIdList(): MutableList<Int> {
        val animalTypeSet = storage.readStringSet(ANIMAL_TYPE_KEY, null)
        return animalTypeSet
            ?.map {
                it.toInt()
            }
            ?.toMutableList() ?: mutableListOf()
    }

    override fun saveAnimalTypeIdList(animalTypeId: MutableList<Int>) {
        val animalTypeIdSet = animalTypeId
            .map {
                it.toString()
            }
            .toSet()
        storage.writeStringSet(ANIMAL_TYPE_KEY, animalTypeIdSet)
    }

    override fun getCityIdList(): MutableList<Int> {
        val cityIdSet = storage.readStringSet(CITY_KEY, null)
        return cityIdSet
            ?.map {
                it.toInt()
            }
            ?.toMutableList() ?: mutableListOf()
    }

    override fun saveCityIdList(cityId: List<Int>) {
        val cityIdSet = cityId
            .map {
                it.toString()
            }
            .toSet()
        storage.writeStringSet(CITY_KEY, cityIdSet)
    }

    override fun getBreedIdList(): MutableList<Int> {
        val breedIdSet = storage.readStringSet(BREED_KEY, null)
        return breedIdSet
            ?.map {
                it.toInt()
            }
            ?.toMutableList() ?: mutableListOf()
    }

    override fun saveBreedIdList(breedId: List<Int>) {
        val breedIdSet = breedId
            .map {
                it.toString()
            }
            .toSet()
        storage.writeStringSet(BREED_KEY, breedIdSet)
    }

    override fun getColorIdList(): MutableList<Int> {
        val characteristicIddSet = storage.readStringSet(COLOR_KEY, null)
        return characteristicIddSet
            ?.map {
                it.toInt()
            }
            ?.toMutableList() ?: mutableListOf()
    }

    override fun saveColorIdList(colorId: List<Int>) {
        val characteristicIdSet = colorId
            .map {
                it.toString()
            }
            .toSet()
        storage.writeStringSet(COLOR_KEY, characteristicIdSet)
    }

    override fun getGenderId(): Int {
        return storage.readInt(GENDER_KEY, -1)
    }

    override fun saveGenderIdList(genderId: Int) {
        storage.writeInt(GENDER_KEY, genderId)
    }

    override fun getMinAge(): Int {
        return storage.readInt(MIN_AGE_KEY, NO_AGE_FILTER_VALUE)
    }

    override fun saveMinAge(minAge: Int) {
        storage.writeInt(MIN_AGE_KEY, minAge)
    }

    override fun getMaxAge(): Int {
        return storage.readInt(MAX_AGE_KEY, NO_AGE_FILTER_VALUE)
    }

    override fun saveMaxAge(maxAge: Int) {
        storage.writeInt(MAX_AGE_KEY, maxAge)
    }

    override fun removeAll() {
        storage.cleanAllData()
    }
}