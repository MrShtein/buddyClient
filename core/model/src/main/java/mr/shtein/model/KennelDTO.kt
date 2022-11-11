package mr.shtein.model

data class KennelDTO(
    val id: Int,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val email: String,
    val avatarUrl: String,
    val coordinates: CoordinatesDTO,
)
