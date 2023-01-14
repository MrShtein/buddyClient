package mr.shtein.kennel.presentation.state.kennel_settings

data class StreetState (
    val streetName: String = "",
    val validationState: ValidationState? = null
)
