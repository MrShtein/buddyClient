package mr.shtein.kennel.presentation.state.kennel_settings

import mr.shtein.kennel.CityField

sealed class CityFieldState {
    object Validate : CityFieldState()
    data class Value(val value: CityField) : CityFieldState()
    data class Error(val message: String) : CityFieldState()
}
