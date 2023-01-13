package mr.shtein.kennel.presentation.state.kennel_settings

data class EmailFieldState(
    var email: String = "",
    var validationState: ValidationState? = null
)
