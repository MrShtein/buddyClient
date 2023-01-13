package mr.shtein.kennel.presentation.state.kennel_settings

data class HouseNumberState(
    val houseNum: String = "",
    val validationState: ValidationState? = null
)
