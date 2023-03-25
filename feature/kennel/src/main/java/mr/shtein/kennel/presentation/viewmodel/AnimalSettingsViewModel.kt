package mr.shtein.kennel.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import mr.shtein.data.exception.BadTokenException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.Animal
import mr.shtein.data.repository.AnimalRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.domain.interactor.AnimalInteractor
import mr.shtein.kennel.R
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.delete_animal.DeleteAnimalState
import mr.shtein.kennel.presentation.state.delete_animal.Deleted
import mr.shtein.kennel.presentation.state.delete_animal.ErrorInfo
import mr.shtein.kennel.presentation.state.delete_animal.Loading
import java.net.ConnectException
import java.net.SocketTimeoutException


class AnimalSettingsViewModel(
    private val animalId: Long,
    private val networkAnimalRepository: AnimalRepository,
    private val animalInteractor: AnimalInteractor,
    private val userPropertiesRepository: UserPropertiesRepository,
    private val navigator: KennelNavigation,
    private val application: Application
) : ViewModel() {


    private val _deleteState = MutableLiveData<DeleteAnimalState>()
    val deleteState: LiveData<DeleteAnimalState>
        get() = _deleteState

    private val _dialogState = MutableLiveData<Boolean>(false)
    val dialogState: LiveData<Boolean>
        get() = _dialogState

    private val _selectedAnimal = MutableLiveData<Animal>()
    val selectedAnimal: LiveData<Animal>
        get() = _selectedAnimal


    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is BadTokenException -> {
                userPropertiesRepository.saveUserToken("")
                val errorText = application.getString(R.string.bad_token_msg)
                _deleteState.value = ErrorInfo(errorText)
            }
            is ServerErrorException -> {
                val errorText = application.getString(R.string.server_unavailable_msg)
                _deleteState.value = ErrorInfo(errorText)
            }
            is ConnectException -> {
                val errorText = application.getString(R.string.internet_failure_text)
                _deleteState.value = ErrorInfo(errorText)
            }
            is SocketTimeoutException -> {
                val errorText = application.getString(R.string.internet_failure_text)
                _deleteState.value = ErrorInfo(errorText)
            }
        }
    }
    init {
        viewModelScope.launch(exceptionHandler) {
            _selectedAnimal.value = animalInteractor.getAnimalById(animalId)
        }
    }

    fun onAnimalUpdated(updatedAnimal: Animal) {
        _selectedAnimal.value = updatedAnimal
    }

    fun onDeleteDialogShowBtnClick() {
        _dialogState.value = true
    }

    fun changeAnimalInfo() {
        _selectedAnimal.value?.let {
            navigator.moveToAddAnimal(animal = it, isFromSettings = true)
        }
    }

    fun deleteAnimal() {
        viewModelScope.launch(exceptionHandler) {
            _deleteState.value = Loading
            _selectedAnimal.value.let { animal ->
                networkAnimalRepository.deleteAnimal(
                    userPropertiesRepository.getUserToken(), animal!!.id
                )
            }
            _deleteState.value = Deleted(animalId)
            navigator.backToPreviousFragment()
        }
    }

}