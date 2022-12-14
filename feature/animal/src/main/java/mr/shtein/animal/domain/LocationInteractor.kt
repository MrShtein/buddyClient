package mr.shtein.animal.domain

import mr.shtein.data.exception.LocationServiceException
import mr.shtein.data.model.Coordinates
import kotlin.jvm.Throws

interface LocationInteractor {
    @Throws(LocationServiceException::class)
    suspend fun getCurrentDistance(): Coordinates
}