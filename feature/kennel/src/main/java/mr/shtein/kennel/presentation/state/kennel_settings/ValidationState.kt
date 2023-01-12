package mr.shtein.kennel.presentation.state.kennel_settings

sealed class ValidationState {
    object Valid : ValidationState()
    data class Invalid(
        val message: String
    ) : ValidationState()
}
