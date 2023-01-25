package mr.shtein.kennel.presentation.state.kennel_home

data class KennelHomeHeaderUiState(
    val kennelAvatarUrl: String,
    val volunteersAmount: Int,
    val animalsAmount: Int,
    val kennelName: String,
)