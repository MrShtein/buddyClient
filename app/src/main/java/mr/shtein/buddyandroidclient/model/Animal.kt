package mr.shtein.buddyandroidclient.model

data class Animal(
    val id: Long,
    val imgUrl: String,
    val spices: String,
    val name: String,
    val kennelName: String,
    val gender: String,
    val age: Int,
    val description: String,
    val breed: String,
    val characteristics: Map<String, String>
)
