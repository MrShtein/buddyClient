package mr.shtein.kennel.presentation.state.add_kennel

import mr.shtein.data.model.KennelPreview

sealed class AddKennelState {
    object Loading: AddKennelState()
    object NoItem: AddKennelState()
    data class Success(val kennelsList: List<KennelPreview>): AddKennelState()
    data class Failure(val message: Int): AddKennelState()
}
