package mr.shtein.kennel.presentation.state.volunteers_list

import mr.shtein.model.volunteer.VolunteerBids

data class VolunteersListState(
    val kennelName: String,
    val volunteersListBodyState: VolunteersListBodyState
)

sealed class VolunteersListBodyState {
    data object Loading: VolunteersListBodyState()
    data object NoItem: VolunteersListBodyState()
    data class Success(val volunteersList: List<VolunteerBids>): VolunteersListBodyState()
    data class Failure(val message: Int) : VolunteersListBodyState()
}
