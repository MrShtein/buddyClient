package mr.shtein.util.state

sealed class VolunteerBidsState<out T> {
    data object Loading: VolunteerBidsState<Nothing>()
    data class Failure(val messageResId: Int): VolunteerBidsState<Nothing>()
    data class Success<T>(val bids: T): VolunteerBidsState<T>()
}
