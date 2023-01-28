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
import mr.shtein.data.model.KennelPreview
import mr.shtein.kennel.R
import mr.shtein.kennel.domain.KennelInteractor
import mr.shtein.kennel.presentation.state.kennel_home.AnimalListState
import mr.shtein.kennel.presentation.state.kennel_home.KennelHomeHeaderUiState
import java.io.IOException

class KennelHomeViewModel(
    private val kennelPreview: KennelPreview,
    private val kennelInteractor: KennelInteractor
) : ViewModel() {

    private val _dogListState: MutableStateFlow<AnimalListState> =
        MutableStateFlow(AnimalListState.Loading)
    val dogListState: StateFlow<AnimalListState> = _dogListState.asStateFlow()

    private val _catListState: MutableStateFlow<AnimalListState> =
        MutableStateFlow(AnimalListState.Loading)
    val catListState: StateFlow<AnimalListState> = _catListState.asStateFlow()

    private val _kennelHomeHeaderState: MutableStateFlow<KennelHomeHeaderUiState> =
        MutableStateFlow(
            value = KennelHomeHeaderUiState(
                kennelAvatarUrl = kennelPreview.avatarUrl,
                volunteersAmount = kennelPreview.volunteersAmount,
                animalsAmount = kennelPreview.animalsAmount,
                kennelName = kennelPreview.name
            )
        )
    val kennelHomeHeaderUiState: StateFlow<KennelHomeHeaderUiState> =
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
                } catch (ex: ServerErrorException) {
                    _catListState.update {
                        AnimalListState.Failure(messageResId = R.string.server_error_msg)
                    }
                }
            }
        }
    }

    companion object {
        private const val DOG_TYPE = "Собаки"
        private const val CAT_TYPE = "Кошки"
    }
}