package mr.shtein.data.repository

import android.content.res.Resources.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.BidExistException
import mr.shtein.data.exception.IncorrectDataException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.model.*
import mr.shtein.network.NetworkService
import okhttp3.MediaType
import okhttp3.RequestBody

class NetworkUserRepository(private val networkService: NetworkService) : UserRepository {

    override suspend fun resetPassword(email: String): String? = withContext(Dispatchers.IO) {
        val result = networkService.resetPassword(email)
        return@withContext result.body()
    }

    override suspend fun signIn(person: Person): LoginResponse = withContext(Dispatchers.IO) {
        val result = networkService.loginUser(person)
        when (result.code()) {
            200 -> {
                return@withContext result.body()!!
            }
            404 -> {
                throw IncorrectDataException()
            }
            else -> {
                throw ServerErrorException()
            }
        }
    }

    override suspend fun signUp(person: Person) = withContext(Dispatchers.IO) {
        val result = networkService.registerUser(person)
        when (result.code()) {
            201 -> {
                return@withContext
            }
            400 -> {
                throw IncorrectDataException()
            }
            else -> throw ServerErrorException()
        }
    }

    override suspend fun updatePersonInfo(
        header: HashMap<String, String>,
        personRequest: PersonRequest
    ): PersonResponse = withContext(Dispatchers.IO) {
        val result = networkService.updatePersonInfo(headers = header, person = personRequest)
        when (result.code()) {
            200 -> {
                return@withContext result.body()!!
            }
            else -> throw ServerErrorException()
        }
    }

    override suspend fun isEmailExist(emailCheckRequest: EmailCheckRequest): Boolean =
        withContext(Dispatchers.IO) {
            val result = networkService.isEmailExists(emailCheckRequest = emailCheckRequest)
            when (result.code()) {
                200 -> {
                    return@withContext result.body()!!
                }
                else -> {
                    throw ServerErrorException()
                }
            }
        }

    override suspend fun checkOldPassword(
        headerMap: Map<String, String>,
        passwordCheckRequest: PasswordCheckRequest
    ): Boolean = withContext(Dispatchers.IO) {
        val result = networkService.checkOldPassword(
            headerMap = headerMap,
            passwordCheckRequest = passwordCheckRequest
        )
        when (result.code()) {
            200 -> {
                return@withContext result.body()!!
            }
            else -> throw ServerErrorException()
        }
    }

    override suspend fun addBidToBecomeVolunteer(token: String, kennelId: Int) =
        withContext(Dispatchers.IO) {
            val result = networkService.addVolunteerBid(
                token = token, kennelId = kennelId
            )
            when (result.code()) {
                201 -> {
                    return@withContext
                }
                404 -> {
                    throw NotFoundException()
                }
                409 -> {
                    throw BidExistException()
                }
                else -> {
                    throw ServerErrorException()
                }
            }
        }

    override suspend fun addUserAvatar(
        token: String,
        avatar: ByteArray,
        contentType: String
    ): String =
        withContext(Dispatchers.IO) {
            val requestBody = RequestBody.create(MediaType.get(contentType), avatar)
            val result = networkService.addUserAvatar(
                avatar = requestBody, token = token
            )
            when (result.code()) {
                201 -> {
                    return@withContext result.body()!!
                }
                else -> {
                    throw ServerErrorException()
                }
            }
        }
}
