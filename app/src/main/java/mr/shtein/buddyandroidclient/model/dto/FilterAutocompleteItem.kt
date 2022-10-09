package mr.shtein.buddyandroidclient.model.dto

data class FilterAutocompleteItem(
    val id: Int,
    val name: String,
    var isSelected: Boolean
) {
    override fun toString(): String {
        return name
    }
}
