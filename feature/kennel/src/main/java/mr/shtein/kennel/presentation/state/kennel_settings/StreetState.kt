package mr.shtein.kennel.presentation.state.kennel_settings

sealed class StreetState {
    object Validate : StreetState()
    data class Value(val value: String) : StreetState()
    data class Error(val message: String, val wrongValue: String) : StreetState()
}
