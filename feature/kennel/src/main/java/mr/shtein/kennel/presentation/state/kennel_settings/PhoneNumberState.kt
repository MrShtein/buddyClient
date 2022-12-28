package mr.shtein.kennel.presentation.state.kennel_settings

sealed class PhoneNumberState {
    data class Value(val value: String) : PhoneNumberState()
    data class Error(val message: String, val wrongValue: String) : PhoneNumberState()
}