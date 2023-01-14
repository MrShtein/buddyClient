package mr.shtein.kennel.presentation.state.kennel_settings

data class KennelNameState(
    val kennelName: String = "",
    val validationState: ValidationState? = null
)
