package mr.shtein.data.mapper

import mr.shtein.data.model.Kennel
import mr.shtein.model.KennelDTO

class KennelMapper(
    private val coordinatesMapper: CoordinatesMapper
) {

    fun transformToDTO(kennel: Kennel): KennelDTO {
        return KennelDTO(
            id = kennel.id,
            name = kennel.name,
            address = kennel.address,
            phoneNumber = kennel.phoneNumber,
            email = kennel.email,
            avatarUrl = kennel.avatarUrl,
            coordinates = coordinatesMapper.transformToDTO(coordinates = kennel.coordinates)
        )
    }

    fun transformFromDTO(kennelDTO: KennelDTO): Kennel {
        return Kennel(
            id = kennelDTO.id,
            name = kennelDTO.name,
            address = kennelDTO.address,
            phoneNumber = kennelDTO.phoneNumber,
            email = kennelDTO.email,
            avatarUrl = kennelDTO.avatarUrl,
            coordinates = coordinatesMapper
                .transformFromDTO(coordinatesDTO = kennelDTO.coordinates)
        )
    }

}
