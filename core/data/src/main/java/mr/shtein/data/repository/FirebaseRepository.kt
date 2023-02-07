package mr.shtein.data.repository

interface FirebaseRepository {
    suspend fun getUserToken(): String
    suspend fun sendErrorToCrashlytics(throwable: Throwable)
}