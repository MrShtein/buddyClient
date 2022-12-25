package mr.shtein.kennel.presentation.state.kennel_settings

sealed class HouseNumberState {
    object Validate : HouseNumberState()
    data class Value(val value: String) : HouseNumberState()
    data class Error(val message: String, val wrongValue: String) : HouseNumberState()
}
