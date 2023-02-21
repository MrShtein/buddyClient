package mr.shtein.data.repository

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.ItemAlreadyExistException
import mr.shtein.data.exception.NoAuthorizationException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.AvatarWrapper
import mr.shtein.data.model.KennelRequest
import mr.shtein.model.KennelPreviewResponse
import mr.shtein.model.volunteer.VolunteersBid
import mr.shtein.network.NetworkService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException

class NetworkKennelRepository(
    private val networkService: NetworkService,
    private val context: Context
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

    override suspend fun getKennelAvatar(avatarUriStr: String): AvatarWrapper? =
        withContext(Dispatchers.IO) {
            return@withContext if (avatarUriStr != "") {
                val avatarUri = Uri.parse(avatarUriStr)
                val file = File(context.filesDir, KENNEL_AVATAR_FILE_NAME)
                try {
                    val imgStream = context.contentResolver.openInputStream(avatarUri)
                    val imgType = context.contentResolver.getType(avatarUri)
                    file.writeBytes(imgStream!!.readBytes())
                    imgStream.close()
                    AvatarWrapper(file, imgType!!)
                } catch (ex: FileNotFoundException) {
                    return@withContext null
                } catch (ex: NullPointerException) {
                    return@withContext null
                }
            } else {
                null;
            }
        }

    override suspend fun getVolunteerBids(token: String, kennelId: Int): List<VolunteersBid> =
        withContext(Dispatchers.IO) {
            val result = networkService.getVolunteerBidByKennel(token = token, kennelId = kennelId)
            when (result.code()) {
                200 -> {
                    return@withContext result.body()!!
                }
                403 -> {
                    throw NoAuthorizationException()
                }
                else -> {
                    throw ServerErrorException()
                }
            }
        }

    companion object {
        private const val KENNEL_AVATAR_FILE_NAME = "kennel_avatar"
    }
}