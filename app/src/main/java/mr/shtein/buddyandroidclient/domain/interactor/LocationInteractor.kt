package mr.shtein.buddyandroidclient.domain.interactor

import mr.shtein.buddyandroidclient.model.Coordinates

interface LocationInteractor {
    suspend fun getCurrentDistance(): Coordinates
}