package mr.shtein.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.ItemAlreadyExistException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.model.KennelPreviewResponse
import mr.shtein.network.NetworkService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NetworkKennelRepository(
    private val networkService: NetworkService
) : KennelRepository {

    override suspend fun addNewKennel(
        headers: Map<String, String>,
        kennelRequest: RequestBody,
        file: MultipartBody.Part?
    ) = withContext(Dispatchers.IO) {
        val result = networkService.addNewKennel(
            headers = headers,
            kennelRequest = kennelRequest,
            file = file
        )
        when (result.code()) {
            201 -> {
                return@withContext
            }
            409 -> {
                throw ItemAlreadyExistException()
            }
            else -> throw ServerErrorException()
        }
    }

    override suspend fun getKennelsByPersonId(
        token: String,
        personId: Long
    ): List<KennelPreviewResponse> = withContext(Dispatchers.IO) {
        val result = networkService.getKennelsByPersonId(
            token = token,
            personId = personId
        )
        when (result.code()) {
            200 -> {
                return@withContext result.body()!!
            }
            else -> throw ServerErrorException()
        }
    }
}