package mr.shtein.data.mapper

import mr.shtein.data.model.Coordinates
import mr.shtein.model.CoordinatesDTO

class CoordinatesMapper {

    fun transformToDTO(coordinates: Coordinates): CoordinatesDTO {
        return CoordinatesDTO(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude
        )
    }

    fun transformFromDTO(coordinatesDTO: CoordinatesDTO): Coordinates {
        return Coordinates(
            latitude = coordinatesDTO.latitude,
            longitude = coordinatesDTO.longitude
        )
    }

}
