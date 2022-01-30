package mr.shtein.buddyandroidclient.model

data class PersonRequest(
    val id: Long,
    val name: String,
    val surname: String,
    val gender: String,
    val city_id: Int,
    val phoneNumber: String,
    val email: String,
    val password: String
)
