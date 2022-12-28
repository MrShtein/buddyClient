package mr.shtein.kennel.presentation.state.kennel_settings

sealed class OrganizationNameState {
    data class Value(val value: String) : OrganizationNameState()
    data class Error(val message: String, val wrongValue: String) : OrganizationNameState()
}
