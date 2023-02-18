package mr.shtein.kennel.presentation.state.kennel_home

import mr.shtein.data.model.Animal
import mr.shtein.model.volunteer.VolunteersBid

sealed class VolunteersBtnState {
    data object Loading: VolunteersBtnState()
    data class Failure(val messageResId: Int): VolunteersBtnState()
    data class Success(val animalList: List<VolunteersBid>): VolunteersBtnState()
}
