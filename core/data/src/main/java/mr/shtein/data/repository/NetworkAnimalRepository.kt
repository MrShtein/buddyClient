package mr.shtein.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.BadCredentialsException
import mr.shtein.data.exception.BadTokenException
import mr.shtein.data.exception.EmptyBodyException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.mapper.AnimalMapper
import mr.shtein.data.model.Animal
import mr.shtein.data.model.AnimalFilter
import mr.shtein.model.AddOrUpdateAnimal
import mr.shtein.model.AnimalDTO
import mr.shtein.network.NetworkService
import okhttp3.MediaType
import okhttp3.RequestBody

private const val CREATED_CODE = 201

class NetworkAnimalRepository(
    private val networkService: NetworkService,
    private val animalMapper: AnimalMapper
) : AnimalRepository {

    private var cashForAnimalById: MutableMap<Long, AnimalDTO> = mutableMapOf()

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

    override suspend fun addNewAnimal(
        token: String,
        addOrUpdateAnimalRequest: AddOrUpdateAnimal
    ): Animal =
        withContext(Dispatchers.IO) {
            val result = networkService.addNewAnimal(
                token = token,
                addOrUpdateAnimalRequest = addOrUpdateAnimalRequest
            )
            when (result.code()) {
                201 -> {
                    val animalDTO: AnimalDTO = result.body()!!
                    cashForAnimalById[animalDTO.id] = animalDTO
                    return@withContext animalMapper.transformFromDTO(animalDTO)
                }
                403 -> {
                    throw BadTokenException()
                }
                else -> {
                    throw ServerErrorException()
                }
            }
        }

    override suspend fun deleteAnimal(token: String, animalId: Long): Unit =
        withContext(Dispatchers.IO) {
            val result = networkService.deleteAnimal(token = token, animalId = animalId)
            when (result.code()) {
                200 -> {
                    return@withContext
                }
                403 -> {
                    throw BadTokenException()
                }
                else -> {
                    throw ServerErrorException()
                }
            }
        }

    override suspend fun updateAnimal(
        token: String,
        addOrUpdateAnimalRequest: AddOrUpdateAnimal
    ): Animal = withContext(Dispatchers.IO) {
        val result = networkService.updateAnimal(
            token = token,
            addOrUpdateAnimalRequest = addOrUpdateAnimalRequest
        )
        when (result.code()) {
            200 -> {
                val animalDTO: AnimalDTO = result.body() ?: throw EmptyBodyException("")
                return@withContext animalMapper.transformFromDTO(animalDTO)
            }
            403 -> {
                throw BadTokenException()
            }
            else -> {
                throw ServerErrorException()
            }
        }
    }

    override suspend fun addPhotoToTmpDir(token: String, image: ByteArray): String =
        withContext(Dispatchers.IO) {
            val requestBody = RequestBody.create(MediaType.get(IMAGE_CONTENT_TYPE), image)

            val result = networkService.addPhotoToTmpDir(
                token = token,
                bytes = requestBody
            )
            when (result.code()) {
                201 -> {
                    return@withContext result.body()!!
                }
                403 -> {
                    throw BadTokenException()
                }
                else -> {
                    throw ServerErrorException()
                }
            }
        }

    override suspend fun getAnimalsByKennelIdAndAnimalType(
        token: String,
        kennelId: Int,
        animalType: String,
        refresh: Boolean
    ): MutableList<AnimalDTO> {
        if (refresh) {
            val result = networkService.getAnimalsByKennelIdAndAnimalType(
                token = token,
                kennelId = kennelId,
                animalType = animalType
            )
            when (result.code()) {
                200 -> {
                    val animalDTOList = result.body() ?: listOf()
                    animalDTOList.forEach { animal ->
                        cashForAnimalById[animal.id] = animal
                    }
                    return result.body()!!
                }
                else -> {
                    throw ServerErrorException()
                }
            }
        } else {
            return cashForAnimalById.map {
                it.value
            }.toMutableList()
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

    override suspend fun getAnimalById(animalId: Long): Animal {
        return if (!cashForAnimalById.isNullOrEmpty()) {
            animalMapper.transformFromDTO(cashForAnimalById[animalId]!!)
        } else {
            val result = networkService.getAnimalById(animalId)
            when (result.code()) {
                200 -> {
                    animalMapper.transformFromDTO(result.body()!!)
                }
                403 -> {
                    throw BadCredentialsException()
                }
                else -> {
                    throw ServerErrorException()
                }
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

    companion object {
        private const val IMAGE_CONTENT_TYPE = "image/webp"
        private const val TAG = "NetworkAnimalRepository"
    }
}