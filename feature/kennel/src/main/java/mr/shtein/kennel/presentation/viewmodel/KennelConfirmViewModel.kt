package mr.shtein.kennel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.AvatarWrapper
import mr.shtein.data.model.KennelRequest
import mr.shtein.data.repository.KennelPropertiesRepository
import mr.shtein.data.repository.SharedUserPropertiesRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.domain.KennelInteractor
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.kennel_confirm.KennelConfirmScreenState
import mr.shtein.kennel.presentation.state.kennel_confirm.NewKennelSendingState
import java.io.FileNotFoundException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class KennelConfirmViewModel(
    val kennelRequest: KennelRequest,
    private val userPropertiesRepository: UserPropertiesRepository,
    private val kennelPropertiesRepository: KennelPropertiesRepository,
    private val kennelInteractor: KennelInteractor,
    val navigator: KennelNavigation
) : ViewModel() {

    companion object {
        private const val ADMIN_ROLE_TXT = "ROLE_ADMIN"
    }

    private val _kennelConfirmScreenState: MutableStateFlow<KennelConfirmScreenState> =
        MutableStateFlow(
            KennelConfirmScreenState(kennelRequest = kennelRequest, null)
        )
    val kennelConfirmScreenState: StateFlow<KennelConfirmScreenState> =
        _kennelConfirmScreenState.asStateFlow()

    fun onSaveBtnClick(avatarImg: String) {
        viewModelScope.launch {
            _kennelConfirmScreenState.value = _kennelConfirmScreenState.value.copy(
                sendingState = NewKennelSendingState.Sending
            )
            try {
                val avatarWrapper: AvatarWrapper? = kennelInteractor.getKennelAvatar(avatarImg)
                kennelRequest.userId = userPropertiesRepository.getUserId()
                val newState: NewKennelSendingState = kennelInteractor.addNewKennel(
                    avatarWrapper = avatarWrapper,
                    kennelRequest = kennelRequest
                )
                _kennelConfirmScreenState.value =
                    _kennelConfirmScreenState.value.copy(sendingState = newState)
                if (_kennelConfirmScreenState.value.sendingState is NewKennelSendingState.Success) {
                    avatarWrapper?.file?.delete()
                    userPropertiesRepository.saveUserRole(ADMIN_ROLE_TXT)
                }
            } catch (ex: IOException) {
                val exTextResId: Int = R.string.internet_failure_text
                _kennelConfirmScreenState.value = _kennelConfirmScreenState.value.copy(
                    sendingState = NewKennelSendingState.Failure(message = exTextResId)
                )
            } catch (ex: ServerErrorException) {
                val exTextResId: Int = R.string.server_unavailable_msg
                _kennelConfirmScreenState.value = _kennelConfirmScreenState.value.copy(
                    sendingState = NewKennelSendingState.Failure(message = exTextResId)
                )
            }
        }
    }

    fun onSuccessDialogBtnClick() {
        _kennelConfirmScreenState.value = _kennelConfirmScreenState.value.copy(
            sendingState = null
        )
        kennelPropertiesRepository.saveKennelAvatarUri("")
        navigator.moveToAddAnimalFromKennelConfirm()
    }

    fun onExistDialogBtnClick() {
        _kennelConfirmScreenState.value = _kennelConfirmScreenState.value.copy(
            sendingState = null
        )
        navigator.backToPreviousFragment()
    }
}