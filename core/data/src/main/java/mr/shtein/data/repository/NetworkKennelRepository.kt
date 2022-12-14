package mr.shtein.data.repository

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.ItemAlreadyExistException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.AvatarWrapper
import mr.shtein.data.model.KennelRequest
import mr.shtein.model.KennelPreviewResponse
import mr.shtein.network.NetworkService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NetworkKennelRepository(
    private val networkService: NetworkService
) : KennelRepository {

    override suspend fun addNewKennel(
        token: String,
        kennelRequest: KennelRequest,
        avatarWrapper: AvatarWrapper?
    ) = withContext(Dispatchers.IO) {
        val headers = mutableMapOf<String, String>()
        headers["Authorization"] = token

        val kennelSettings = Gson().toJson(kennelRequest)
        val kennelRequestPart = RequestBody.create(
            MultipartBody.FORM, kennelSettings
        )

        val result = if (avatarWrapper != null) {
            val requestFile = RequestBody.create(
                MediaType.parse(avatarWrapper.fileType),
                avatarWrapper.file
            )

            val filePart = MultipartBody.Part.createFormData(
                KENNEL_AVATAR_FILE_NAME,
                avatarWrapper.file.name,
                requestFile
            )

            networkService.addNewKennel(
                headers = headers,
                kennelRequest = kennelRequestPart,
                file = filePart
            )
        } else {
            networkService.addNewKennel(
                headers = headers,
                kennelRequest = kennelRequestPart,
                file = null
            )
        }
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

    companion object {
        private const val KENNEL_AVATAR_FILE_NAME = "kennel_avatar"
    }
}