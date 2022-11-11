package mr.shtein.buddyandroidclient.data.mapper

import mr.shtein.buddyandroidclient.model.AnimalPhoto
import mr.shtein.model.AnimalPhotoDTO

class AnimalPhotoMapper {

    fun transformToDTOList(animalPhotoList: List<AnimalPhoto>): List<AnimalPhotoDTO> {
        return animalPhotoList
            .map {
                transformToDTO(it)
            }
            .toList()
    }

    private fun transformToDTO(animalPhoto: AnimalPhoto): AnimalPhotoDTO {
        return AnimalPhotoDTO(
            url = animalPhoto.url,
            primary = animalPhoto.primary,
            animalId = animalPhoto.animalId
        )
    }

    private fun transformFromDTO(animalPhotoDTO: AnimalPhotoDTO): AnimalPhoto {
        return AnimalPhoto(
            url = animalPhotoDTO.url,
            primary = animalPhotoDTO.primary,
            animalId = animalPhotoDTO.animalId
        )
    }

    fun transformFromDTOList(animalPhotoDTOList: List<AnimalPhotoDTO>): List<AnimalPhoto> {
        return animalPhotoDTOList
            .map {
                transformFromDTO(it)
            }
            .toList()
    }



}
