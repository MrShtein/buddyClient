package mr.shtein.data.repository

import mr.shtein.data.model.AvatarWrapper
import mr.shtein.data.model.KennelRequest
import mr.shtein.model.KennelPreviewResponse
import mr.shtein.model.volunteer.VolunteersBid
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface KennelAdministratorRepository {

    suspend fun getAllKennelsVolunteerBids(
        token: String,
    ): List<VolunteersBid>

}