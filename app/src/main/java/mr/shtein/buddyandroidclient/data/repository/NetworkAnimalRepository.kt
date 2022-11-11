package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.buddyandroidclient.data.mapper.AnimalMapper
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.AnimalFilter
import mr.shtein.buddyandroidclient.retrofit.NetworkService

class NetworkAnimalRepository(
    private val networkService: NetworkService,
    private val animalMapper: AnimalMapper
    ) : AnimalRepository {

    override suspend fun getAnimals(filter: AnimalFilter): List<Animal> {
        val minAge = getMinAge(filter.minAge)
        val maxAge = getMaxAge(filter.maxAge)
        val gender = getGenderId(filter.genderId)
        val result = networkService.getAnimals(
            animalTypeId = filter.animalTypeId,
            cityId = filter.cityId,
            breedId = filter.breedId,
            genderId = gender,
            characteristicId = filter.colorId,
            minAge = minAge,
            maxAge = maxAge
        )
        return when (result.code()) {
            200 -> {
                val animalDTOList = result.body() ?: listOf()
                return animalMapper.transformFromDTOList(animalDTOList = animalDTOList)
            }
            500 -> {
                throw ServerErrorException()
            }
            else -> {
                listOf()
            }
        }
    }

    override suspend fun getAnimalsCountByFilter(animalFilter: AnimalFilter): Int {
        val minAge = getMinAge(animalFilter.minAge)
        val maxAge = getMaxAge(animalFilter.maxAge)
        val gender = getGenderId(animalFilter.genderId)
        val result = networkService.getAnimalsCountByFilter(
            animalTypeId = animalFilter.animalTypeId,
            cityId = animalFilter.cityId,
            breedId = animalFilter.breedId,
            genderId = gender,
            characteristicId = animalFilter.colorId,
            minAge = minAge,
            maxAge = maxAge
        )
        return when (result.code()) {
            200 -> {
                result.body() ?: 0
            }
            500 -> {
                throw ServerErrorException()
            }
            else -> {
                0
            }
        }
    }

    private fun getMinAge(minAge: Int): Int? {
        return if (minAge == -1) {
            null
        } else {
            minAge
        }
    }

    private fun getMaxAge(maxAge: Int): Int? {
        return if (maxAge == -1) {
            null
        } else {
            maxAge
        }
    }

    private fun getGenderId(genderId: Int): Int? {
        return if (genderId == -1) {
            null
        } else {
            genderId
        }
    }
}