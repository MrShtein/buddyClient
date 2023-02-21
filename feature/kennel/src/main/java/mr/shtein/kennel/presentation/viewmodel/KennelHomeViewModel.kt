package mr.shtein.kennel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.Animal
import mr.shtein.data.model.KennelPreview
import mr.shtein.kennel.R
import mr.shtein.kennel.domain.KennelInteractor
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.kennel_home.AnimalListState
import mr.shtein.kennel.presentation.state.kennel_home.KennelHomeUiState
import mr.shtein.model.volunteer.VolunteersBid
import mr.shtein.util.state.VolunteerBidsState

class KennelHomeViewModel(
    private val kennelPreview: KennelPreview,
    private val kennelInteractor: KennelInteractor,
    private val navigation: KennelNavigation
) : ViewModel() {

    private var dogList: MutableList<Animal> = mutableListOf()
    private var catList: MutableList<Animal> = mutableListOf()

    private val _dogListState: MutableStateFlow<AnimalListState> =
        MutableStateFlow(AnimalListState.Loading)
    val dogListState: StateFlow<AnimalListState> = _dogListState.asStateFlow()

    private val _catListState: MutableStateFlow<AnimalListState> =
        MutableStateFlow(AnimalListState.Loading)
    val catListState: StateFlow<AnimalListState> = _catListState.asStateFlow()

    private val _volunteerBidsState: MutableStateFlow<VolunteerBidsState<List<VolunteersBid>>> =
        MutableStateFlow(VolunteerBidsState.Loading)
    val volunteerBidsState: StateFlow<VolunteerBidsState<List<VolunteersBid>>> = _volunteerBidsState.asStateFlow()

    private val _kennelHomeHeaderState: MutableStateFlow<KennelHomeUiState> =
        MutableStateFlow(
            value = KennelHomeUiState(
                kennelAvatarUrl = kennelPreview.avatarUrl,
                volunteersAmount = kennelPreview.volunteersAmount,
                animalsAmount = kennelPreview.animalsAmount,
                kennelName = kennelPreview.name,
                isDialogVisible = false
            )
        )
    val kennelHomeUiState: StateFlow<KennelHomeUiState> =
        _kennelHomeHeaderState.asStateFlow()

    init {
        val connectionExceptionHandler = CoroutineExceptionHandler { _, _ ->
            _dogListState.update {
                AnimalListState.Failure(messageResId = R.string.internet_failure_text)
            }
        }
        viewModelScope.launch(connectionExceptionHandler) {
            launch {
                try {
                    _dogListState.update {
                        kennelInteractor.getAnimalByTypeIdAndKennelId(
                            animalType = DOG_TYPE,
                            kennelId = kennelPreview.kennelId
                        )
                    }
                    dogList =
                        (_dogListState.value as AnimalListState.Success).animalList.toMutableList()
                } catch (ex: ServerErrorException) {
                    _dogListState.update {
                        AnimalListState.Failure(messageResId = R.string.server_error_msg)
                    }
                }
            }
            launch {
                try {
                    _catListState.update {
                        kennelInteractor.getAnimalByTypeIdAndKennelId(
                            animalType = CAT_TYPE,
                            kennelId = kennelPreview.kennelId
                        )
                    }
                    catList =
                        (_catListState.value as AnimalListState.Success).animalList.toMutableList()
                } catch (ex: ServerErrorException) {
                    _catListState.update {
                        AnimalListState.Failure(messageResId = R.string.server_error_msg)
                    }
                }
            }
            launch {
                _volunteerBidsState.update {
                    kennelInteractor.getVolunteerBids(kennelId = kennelPreview.kennelId)
                }
            }
        }
    }

    fun onNewAnimalAdded(animal: Animal) {
        when (animal.typeId) {
            DOG_ID -> {
                dogList.add(animal)
                _dogListState.update {
                    AnimalListState.Success(animalList = dogList)
                }
                _kennelHomeHeaderState.update { kennelHomeHeaderUiState ->
                    kennelHomeHeaderUiState.copy(
                        animalsAmount = dogList.size + catList.size
                    )
                }
            }
            CAT_ID -> {
                catList.add(animal)
                _catListState.update {
                    AnimalListState.Success(animalList = catList)
                }
                _kennelHomeHeaderState.update { kennelHomeHeaderUiState ->
                    kennelHomeHeaderUiState.copy(
                        animalsAmount = dogList.size + catList.size
                    )
                }
            }
        }
    }

    fun onAddDogBtnClick() {
        if (kennelPreview.isValid) {
            navigation.moveToAddAnimalFromKennelHome(
                kennelId = kennelPreview.kennelId, animalTypeId = DOG_ID
            )
            return
        }
        _kennelHomeHeaderState.update { kennelHomeUiState ->
            kennelHomeUiState.copy(isDialogVisible = true)
        }
    }

    fun onCatDogBtnClick() {
        if (kennelPreview.isValid) {
            navigation.moveToAddAnimalFromKennelHome(
                kennelId = kennelPreview.kennelId, animalTypeId = CAT_ID
            )
            return
        }
        _kennelHomeHeaderState.update { kennelHomeUiState ->
            kennelHomeUiState.copy(isDialogVisible = true)
        }
    }

    fun onAnimalPhotoClick(animalItem: Animal) {
        navigation.moveToAnimalSettings(animal = animalItem)
    }

    fun onCancelDialog() {
        _kennelHomeHeaderState.update { kennelHomeUiState ->
            kennelHomeUiState.copy(isDialogVisible = false)
        }
    }

    companion object {
        private const val DOG_TYPE = "Собаки"
        private const val DOG_ID = 1
        private const val CAT_TYPE = "Кошки"
        private const val CAT_ID = 2
    }
}