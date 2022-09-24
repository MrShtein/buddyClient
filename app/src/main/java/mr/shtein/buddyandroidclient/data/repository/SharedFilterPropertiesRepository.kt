package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.utils.SharedPreferences

const val ANIMAL_TYPE_KEY = "animal_type"
const val CITY_KEY = "city"
const val BREED_KEY = "breed"
const val CHARACTERISTIC_KEY = "characteristic"
private const val GENDER_KEY = "gender"
private const val MIN_AGE_KEY = "min_age"
private const val MAX_AGE_KEY = "max_age"
const val NO_AGE_FILTER_VALUE = -1

class SharedFilterPropertiesRepository(private val storage: SharedPreferences) :
    FilterPropertiesRepository {
    override fun getAnimalTypeId(): MutableList<Int>? {
        val animalTypeSet = storage.readStringSet(ANIMAL_TYPE_KEY, null)
        return animalTypeSet
            ?.map {
                it.toInt()
            }
            ?.toMutableList()
    }

    override fun saveAnimalTypeId(animalTypeId: MutableList<Int>) {
        val animalTypeIdSet = animalTypeId
            .map {
                it.toString()
            }
            .toSet()
        storage.writeStringSet(ANIMAL_TYPE_KEY, animalTypeIdSet)
    }

    override fun getCityId(): MutableList<Int>? {
        val cityIdSet = storage.readStringSet(CITY_KEY, null)
        return cityIdSet
            ?.map {
                it.toInt()
            }
            ?.toMutableList()
    }

    override fun saveCityId(cityId: List<Int>) {
        val cityIdSet = cityId
            .map {
                it.toString()
            }
            .toSet()
        storage.writeStringSet(CITY_KEY, cityIdSet)
    }

    override fun getBreedId(): MutableList<Int>? {
        val breedIdSet = storage.readStringSet(BREED_KEY, null)
        return breedIdSet
            ?.map {
                it.toInt()
            }
            ?.toMutableList()
    }

    override fun saveBreedId(breedId: List<Int>) {
        val breedIdSet = breedId
            .map {
                it.toString()
            }
            .toSet()
        storage.writeStringSet(BREED_KEY, breedIdSet)
    }

    override fun getCharacteristicId(): MutableList<Int>? {
        val characteristicIddSet = storage.readStringSet(CHARACTERISTIC_KEY, null)
        return characteristicIddSet
            ?.map {
                it.toInt()
            }
            ?.toMutableList()
    }

    override fun saveCharacteristicId(characteristicId: List<Int>) {
        val characteristicIdSet = characteristicId
            .map {
                it.toString()
            }
            .toSet()
        storage.writeStringSet(CHARACTERISTIC_KEY, characteristicIdSet)
    }

    override fun getGenderId(): MutableList<Int>? {
        val genderIdSet = storage.readStringSet(GENDER_KEY, null)
        return genderIdSet
            ?.map {
                it.toInt()
            }
            ?.toMutableList()
    }

    override fun saveGenderId(genderId: List<Int>) {
        val genderIdSet = genderId
            .map {
                it.toString()
            }
            .toSet()
        storage.writeStringSet(CHARACTERISTIC_KEY, genderIdSet)
    }

    override fun getMinAge(): Int {
        return storage.readInt(MIN_AGE_KEY, NO_AGE_FILTER_VALUE)
    }

    override fun saveMinAge(minAge: Int) {
        storage.writeInt(MIN_AGE_KEY, -1)
    }

    override fun getMaxAge(): Int {
        return storage.readInt(MAX_AGE_KEY, NO_AGE_FILTER_VALUE)
    }

    override fun saveMaxAge(maxAge: Int) {
        storage.writeInt(MAX_AGE_KEY, -1)
    }

    override fun removeAll() {
        storage.cleanAllData()
    }
}