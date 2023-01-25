package mr.shtein.kennel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mr.shtein.data.model.KennelPreview
import mr.shtein.kennel.presentation.state.kennel_home.KennelHomeHeaderUiState

class KennelHomeViewModel(
    private val kennelPreview: KennelPreview
) : ViewModel() {

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


}