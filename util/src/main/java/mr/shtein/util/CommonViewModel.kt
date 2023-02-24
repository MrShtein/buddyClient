package mr.shtein.util

import android.content.res.Resources.NotFoundException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.repository.FirebaseRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.model.volunteer.VolunteersBid
import mr.shtein.util.di.KennelAdministratorInteractor
import mr.shtein.util.state.BottomNavState
import mr.shtein.util.state.VolunteerBidsState
import java.io.IOException

class CommonViewModel(
    private val userPropertiesRepository: UserPropertiesRepository,
    private val kennelAdministratorInteractor: KennelAdministratorInteractor,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    companion object {
        private val TAG = "CommonViewModel"
    }

    var bottomNavHeight: Int = 0

    private val _bidBadgesVisibilityState: MutableStateFlow<BottomNavState> = MutableStateFlow(
        BottomNavState(
            isKennelBadgeVisible = false,
            volunteerBidsState = VolunteerBidsState.Loading
        )
    )

    val bidBadgesVisibilityState: StateFlow<BottomNavState> = _bidBadgesVisibilityState.asStateFlow()

    init {
        val token = userPropertiesRepository.getUserToken()
        if (token.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    val volunteerBidsState: VolunteerBidsState<Map<Int, List<VolunteersBid>>> =
                        kennelAdministratorInteractor.getAllKennelsVolunteerBids()
                    if (volunteerBidsState is VolunteerBidsState.Success
                        && volunteerBidsState.bids.isNotEmpty()
                    ) {
                        _bidBadgesVisibilityState.update {
                            it.copy(
                                isKennelBadgeVisible = true,
                                volunteerBidsState = volunteerBidsState
                            )
                        }
                    }
                } catch (ex: NotFoundException) {
                    firebaseRepository.sendErrorToCrashlytics(ex)
                } catch (ex: ServerErrorException) {
                    firebaseRepository.sendErrorToCrashlytics(ex)
                } catch (ex: IOException) {
                    firebaseRepository.sendErrorToCrashlytics(ex)
                }
            }
        }
    }
}