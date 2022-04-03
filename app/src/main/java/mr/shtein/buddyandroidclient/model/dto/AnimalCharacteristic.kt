package mr.shtein.buddyandroidclient.model.dto

data class AnimalCharacteristic(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}
