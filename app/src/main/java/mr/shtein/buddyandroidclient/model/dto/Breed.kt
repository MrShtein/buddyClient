package mr.shtein.buddyandroidclient.model.dto

data class Breed(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}
