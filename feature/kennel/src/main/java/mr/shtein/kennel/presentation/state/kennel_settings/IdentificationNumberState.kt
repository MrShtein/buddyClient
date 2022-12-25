package mr.shtein.kennel.presentation.state.kennel_settings

sealed class IdentificationNumberState {
    object Validate : IdentificationNumberState()
    data class Value(val value: String) : IdentificationNumberState()
    data class Error(val message: String, val wrongValue: String) : IdentificationNumberState()
}
