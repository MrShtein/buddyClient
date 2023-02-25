package mr.shtein.kennel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.volunteers_list.VolunteersListBodyState
import mr.shtein.kennel.presentation.state.volunteers_list.VolunteersListState

class VolunteerListViewModel(
    val navigator: KennelNavigation
) : ViewModel() {

    private val _volunteerListState: MutableStateFlow<VolunteersListState> =
        MutableStateFlow(
            VolunteersListState(
                kennelName = "", volunteersListBodyState = VolunteersListBodyState.Loading
            )
        )
    val volunteerListState: StateFlow<VolunteersListState> = _volunteerListState.asStateFlow()

    fun onFragmentCreate(kennelName: String) {
        _volunteerListState.update {
            it.copy(
                kennelName = kennelName
            )
        }
    }
}