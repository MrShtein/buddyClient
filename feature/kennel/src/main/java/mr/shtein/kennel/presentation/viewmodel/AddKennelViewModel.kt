package mr.shtein.kennel.presentation.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.KennelPreview
import mr.shtein.kennel.R
import mr.shtein.kennel.domain.KennelInteractor
import mr.shtein.kennel.domain.KennelInteractorImpl
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.AddKennelState
import mr.shtein.network.ImageLoader
import java.net.ConnectException
import java.net.SocketTimeoutException

class AddKennelViewModel(
    private val dispatcher: CoroutineDispatcher,
    private val navigator: KennelNavigation,
    private val kennelInteractorImpl: KennelInteractor
) : ViewModel() {

    private val _addKennelState: MutableLiveData<AddKennelState> = MutableLiveData()
    val addKennelState: LiveData<AddKennelState> = _addKennelState

    init {
        _addKennelState.value = AddKennelState.Loading
    }

    fun loadKennels() {
        _addKennelState.value = AddKennelState.Loading
        viewModelScope.launch(dispatcher) {
            try {
                _addKennelState.value = kennelInteractorImpl.loadKennelsListByPersonId()
            } catch (error: ServerErrorException) {
                _addKennelState.value = AddKennelState.Failure(R.string.server_unavailable_msg)
            } catch (error: SocketTimeoutException) {
                _addKennelState.value = AddKennelState.Failure(R.string.internet_failure_text)
            } catch (error: ConnectException) {
                _addKennelState.value = AddKennelState.Failure(R.string.internet_failure_text)
            }
        }
    }

    fun onAddKennelBtnClicked() {
        navigator.moveToKennelSettings()
    }

    fun onKennelItemClicked(kennelItem: Bundle) {
        navigator.moveToKennelHome(kennelItem)
    }

}