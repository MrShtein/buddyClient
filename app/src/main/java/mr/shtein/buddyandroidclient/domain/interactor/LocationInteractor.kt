package mr.shtein.buddyandroidclient.domain.interactor

import mr.shtein.data.exception.LocationServiceException
import kotlin.jvm.Throws

interface LocationInteractor {
    @Throws(LocationServiceException::class)
    suspend fun getCurrentDistance(): mr.shtein.data.model.Coordinates
}