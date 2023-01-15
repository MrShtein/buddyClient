package mr.shtein.kennel.presentation.state.kennel_settings

import mr.shtein.kennel.CityField

data class CityFieldState(
    var field: CityField,
    var validationState: ValidationState? = null
)
