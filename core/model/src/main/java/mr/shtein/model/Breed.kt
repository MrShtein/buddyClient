package mr.shtein.model

data class Breed(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}
