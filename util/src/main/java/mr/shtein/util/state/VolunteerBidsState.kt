package mr.shtein.util.state

import mr.shtein.model.volunteer.VolunteersBid

sealed class VolunteerBidsState {
    data object Loading: VolunteerBidsState()
    data class Failure(val messageResId: Int): VolunteerBidsState()
    data class Success(val animalList: List<VolunteersBid>): VolunteerBidsState()
}
