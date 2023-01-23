package mr.shtein.kennel.presentation.state.kennel_confirm

sealed class NewKennelSendingState {
    data object Sending: NewKennelSendingState()
    data object Success: NewKennelSendingState()
    data object Exist: NewKennelSendingState()
    data class Failure(val message: Int): NewKennelSendingState()
}