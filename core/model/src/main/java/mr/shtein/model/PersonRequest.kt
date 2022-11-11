package mr.shtein.model

data class PersonRequest(
    val id: Long,
    val name: String,
    val surname: String,
    val gender: String,
    val city_id: Long,
    val phoneNumber: String,
    val email: String,
    val password: String
)
