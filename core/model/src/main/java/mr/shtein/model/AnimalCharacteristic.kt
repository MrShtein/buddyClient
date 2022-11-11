package mr.shtein.model

data class AnimalCharacteristic(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}
