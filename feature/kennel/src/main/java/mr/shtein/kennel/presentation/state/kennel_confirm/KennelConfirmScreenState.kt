package mr.shtein.kennel.presentation.state.kennel_confirm

import mr.shtein.data.model.KennelRequest

data class KennelConfirmScreenState(
    val kennelRequest: KennelRequest,
    val sendingState: NewKennelSendingState? = null
)
