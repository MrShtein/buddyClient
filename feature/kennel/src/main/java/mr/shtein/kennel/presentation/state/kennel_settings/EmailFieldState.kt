package mr.shtein.kennel.presentation.state.kennel_settings

sealed class EmailFieldState {
    object Validate : EmailFieldState()
    data class Value(val value: String) : EmailFieldState()
    data class Error(val message: String, val wrongValue: String) : EmailFieldState()
}
