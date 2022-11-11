package mr.shtein.buddyandroidclient.data.mapper

import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.LocationState
import mr.shtein.model.AnimalDTO

class AnimalMapper(
    private val animalPhotoMapper: AnimalPhotoMapper,
    private val kennelMapper: KennelMapper
) {

    private fun transformToDTO(animal: Animal): AnimalDTO {
        return AnimalDTO(
            id = animal.id,
            imgUrl = animalPhotoMapper.transformToDTOList(animalPhotoList = animal.imgUrl),
            typeId = animal.typeId,
            name = animal.name,
            gender = animal.gender,
            age = animal.age,
            description = animal.description,
            breed = animal.breed,
            characteristics = animal.characteristics.toMap(),
            kennel = kennelMapper.transformToDTO(kennel = animal.kennel),
            distance = animal.distance
        )
    }

    private fun transformFromDTO(animalDTO: AnimalDTO): Animal {
        return Animal(
            id = animalDTO.id,
            imgUrl = animalPhotoMapper.transformFromDTOList(animalDTO.imgUrl),
            typeId = animalDTO.typeId,
            name = animalDTO.name,
            gender = animalDTO.gender,
            age = animalDTO.age,
            description = animalDTO.description,
            breed = animalDTO.breed,
            characteristics = animalDTO.characteristics.toMap(),
            kennel = kennelMapper.transformFromDTO(kennelDTO = animalDTO.kennel),
            distance = animalDTO.distance,
            locationState = LocationState.INIT_STATE
        )
    }

    fun transformFromDTOList(animalDTOList: List<AnimalDTO>): List<Animal> {
        return animalDTOList
            .map  {
                transformFromDTO(it)
            }
            .toList()
    }

}
