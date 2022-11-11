package mr.shtein.model

data class AnimalType(
    val id: Int,
    val pluralAnimalType: String,
    val animalType: String
) {
    override fun toString(): String {
        return animalType
    }
}
