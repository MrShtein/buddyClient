package mr.shtein.kennel.domain

import mr.shtein.kennel.presentation.state.add_kennel.AddKennelState

interface KennelInteractor {
    suspend fun loadKennelsListByPersonId(): AddKennelState
}