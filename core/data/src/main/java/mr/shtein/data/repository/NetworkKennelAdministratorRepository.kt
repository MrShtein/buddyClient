package mr.shtein.data.repository

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.net.Uri
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
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

class NetworkKennelAdministratorRepository(
    private val networkService: NetworkService,
    private val dispatcher: CoroutineDispatcher
) : KennelAdministratorRepository {

    override suspend fun getAllKennelsVolunteerBids(token: String): List<VolunteersBid> =
        withContext(Dispatchers.IO) {
            val result = networkService.getAllKennelsVolunteerBids(token = token)
            when (result.code()) {
                200 -> {
                    return@withContext result.body()!!
                }
                404 -> {
                    throw NotFoundException()
                }
                else -> {
                    throw ServerErrorException()
                }
            }
        }
}