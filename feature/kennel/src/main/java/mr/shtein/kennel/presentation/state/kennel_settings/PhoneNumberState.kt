package mr.shtein.kennel.presentation.state.kennel_settings

data class PhoneNumberState(
    val phoneNum: String = "",
    val validationState: ValidationState? = null
)