package mr.shtein.model

data class CityChoiceItem(
    val city_id: Int,
    val name: String,
    val region: String
) {
    override fun toString(): String {
        return "$name, $region"
    }
}
