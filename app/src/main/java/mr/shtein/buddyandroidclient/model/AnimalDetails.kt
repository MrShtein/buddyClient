package mr.shtein.buddyandroidclient.model

data class AnimalDetails(
    val id: Long,
    val imgUrl: List<String>,
    val spices: String,
    val name: String,
    val kennelName: String,
    val gender: String,
    val age: Int,
    val description: String,
    val breed: String,
    val kennelPhoneNumber: String,
    val kennelEmail: String,
    val characteristics: Map<String, String>
)
