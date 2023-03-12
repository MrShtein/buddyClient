package mr.shtein.kennel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mr.shtein.data.model.KennelPreview
import mr.shtein.kennel.R
import mr.shtein.kennel.domain.KennelInteractor
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.volunteers_list.VolunteersListBodyState
import mr.shtein.kennel.presentation.state.volunteers_list.VolunteersListState
import mr.shtein.model.volunteer.VolunteersBid
import java.io.IOException

class VolunteerListViewModel(
    val navigator: KennelNavigation,
    val kennelPreview: KennelPreview,
    val kennelInteractor: KennelInteractor
) : ViewModel() {

    private val _volunteerListState: MutableStateFlow<VolunteersListState> =
        MutableStateFlow(
            VolunteersListState(
                kennelName = kennelPreview.name,
                volunteersListBodyState = VolunteersListBodyState.Loading
            )
        )
    val volunteerListState: StateFlow<VolunteersListState> = _volunteerListState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _volunteerListState.update {
                    it.copy(
                        volunteersListBodyState = kennelInteractor.getVolunteersAndBids(
                            kennelId = kennelPreview.kennelId
                        )
                    )
                }
            } catch (ex: IOException) {
                _volunteerListState.update {
                    it.copy(
                        volunteersListBodyState = VolunteersListBodyState.Failure(
                            message = R.string.internet_failure_text
                        )
                    )
                }
            }
        }
    }

    fun onVolunteerCardClick(volunteerId: Long) {
        navigator.moveToVolunteerCard(volunteerId = volunteerId)
    }


}