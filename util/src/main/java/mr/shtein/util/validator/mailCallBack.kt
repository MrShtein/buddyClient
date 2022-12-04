package mr.shtein.util.validator

import java.lang.Exception

interface MailCallback {
    fun onSuccess()
    fun onFail(error: Exception)
    fun onFailure()
    fun onNoAuthorize()
}