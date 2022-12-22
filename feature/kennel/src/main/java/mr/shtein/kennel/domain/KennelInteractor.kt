package mr.shtein.kennel.domain

import mr.shtein.kennel.presentation.state.AddKennelState

interface KennelInteractor {
    suspend fun loadKennelsListByPersonId(): AddKennelState
}