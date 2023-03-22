package mr.shtein.kennel.presentation.viewmodel

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.shtein.data.exception.BadCredentialsException
import mr.shtein.data.exception.BadTokenException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.Animal
import mr.shtein.data.repository.AnimalRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.domain.interactor.AnimalInteractor
import mr.shtein.kennel.R
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.delete_animal.DeleteAnimalState
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException


class AnimalSettingsViewModel(
    private val animalId: Long,
    private val networkAnimalRepository: AnimalRepository,
    private val animalInteractor: AnimalInteractor,
    private val userPropertiesRepository: UserPropertiesRepository,
    private val navigator: KennelNavigation
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

    init {
        viewModelScope.launch {
            try {
                _selectedAnimal.value = animalInteractor.getAnimalById(animalId)
            } catch (ex: BadCredentialsException) {

            } catch (ex: ServerErrorException) {

            } catch (ex: IOException) {

            }catch (ex:Exception){val error ="Error"
                _deleteState.value = DeleteAnimalState.ErrorInfo(error) }
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

    fun deleteAnimal(context: Context, fragment: Fragment) {
        viewModelScope.launch {
            _deleteState.value = DeleteAnimalState.Loading
            _deleteState.value = DeleteAnimalState.Deleted(animalId)
            try {
                _selectedAnimal.value.let { animal ->
                    networkAnimalRepository.deleteAnimal(
                        userPropertiesRepository.getUserToken(), animal!!.id
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

            navigator.backToPreviousFragment()

        }
    }

}