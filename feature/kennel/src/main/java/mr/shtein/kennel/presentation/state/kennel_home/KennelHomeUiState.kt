package mr.shtein.kennel.presentation.state.kennel_home

data class KennelHomeUiState(
    val kennelAvatarUrl: String,
    val volunteersAmount: Int,
    val animalsAmount: Int,
    val kennelName: String,
    val isDialogVisible: Boolean
)