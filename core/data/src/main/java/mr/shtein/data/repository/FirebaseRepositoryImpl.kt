package mr.shtein.data.repository

import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.ServerErrorException
import kotlin.coroutines.resumeWithException

class FirebaseRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val firebaseMessaging: FirebaseMessaging,
    private val firebaseCrashlytics: FirebaseCrashlytics
    ) : FirebaseRepository {
    override suspend fun getUserToken(): String {
        return suspendCancellableCoroutine { continuation ->
            firebaseMessaging.token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(task.result) {}
                    } else {
                        continuation.resumeWithException(ServerErrorException())
                    }
                })
        }
    }

    override suspend fun sendErrorToCrashlytics(throwable: Throwable) = withContext(dispatcher) {
        firebaseCrashlytics.recordException(throwable)
    }
}