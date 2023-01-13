package mr.shtein.kennel.presentation.state.kennel_settings

data class IdentificationNumberState (
    val identificationNum: String = "",
    val validationState: ValidationState? = null
)
