package mr.shtein.kennel.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.shtein.data.exception.BadTokenException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.Animal
import mr.shtein.data.repository.AnimalRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.presentation.state.delete_animal.DeleteAnimalState
import java.net.ConnectException
import java.net.SocketTimeoutException

class AnimalSettingsViewModel(
    private val deletedAnimal: Animal,
    private val networkAnimalRepository: AnimalRepository,
    private val userPropertiesRepository: UserPropertiesRepository
) : ViewModel() {


    private val _deleteState = MutableLiveData<DeleteAnimalState>()
    val deleteState: LiveData<DeleteAnimalState>
        get() = _deleteState

    private val _dialogState = MutableLiveData<Boolean>(false)
    val dialogState: LiveData<Boolean>
        get() = _dialogState

    private val _deleteAnimal = MutableLiveData(deletedAnimal)
    val deleteAnimal:LiveData<Animal>
    get() = _deleteAnimal

    fun onDeleteDialogShowBtnClick() {
        _dialogState.value = true
    }

    fun deleteAnimal(context: Context) {
        viewModelScope.launch {
            _deleteState.value = DeleteAnimalState.Loading
            try {

                deletedAnimal.let { animal ->
                    networkAnimalRepository.deleteAnimal(
                        userPropertiesRepository.getUserToken(), animal.id
                    )
                }
            } catch (ex: BadTokenException) {
                userPropertiesRepository.saveUserToken("")

                val errorText = context.getString(R.string.bad_token_msg)
                _deleteState.value = DeleteAnimalState.ErrorInfo(errorText)
            } catch (ex: ServerErrorException) {
                val errorText = context.getString(R.string.server_unavailable_msg)
                _deleteState.value = DeleteAnimalState.ErrorInfo(errorText)
            } catch (ex: ConnectException) {
                val errorText = context.getString(R.string.internet_failure_text)
                _deleteState.value = DeleteAnimalState.ErrorInfo(errorText)
            } catch (ex: SocketTimeoutException) {
                val errorText = context.getString(R.string.internet_failure_text)
                _deleteState.value = DeleteAnimalState.ErrorInfo(errorText)
            }
        }
    }

}