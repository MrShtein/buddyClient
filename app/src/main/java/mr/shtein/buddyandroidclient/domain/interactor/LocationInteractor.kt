package mr.shtein.buddyandroidclient.domain.interactor

import mr.shtein.buddyandroidclient.exceptions.validate.LocationServiceException
import mr.shtein.buddyandroidclient.model.Coordinates
import kotlin.jvm.Throws

interface LocationInteractor {
    @Throws(LocationServiceException::class)
    suspend fun getCurrentDistance(): Coordinates
}