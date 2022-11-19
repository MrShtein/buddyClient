package mr.shtein.buddyandroidclient.data.repository

import mr.shtein.model.KennelPreviewResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface KennelRepository {

    suspend fun addNewKennel(
        headers: Map<String, String>,
        kennelRequest: RequestBody,
        file: MultipartBody.Part?
    )

    suspend fun getKennelsByPersonId(
       token: String,
       personId: Long
    ): List<KennelPreviewResponse>

}