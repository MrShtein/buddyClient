package mr.shtein.util.validator

import java.lang.Exception

interface PasswordCallBack {
    fun onSuccess()
    fun onFail(error: String)
    fun onFailure()
    fun onNoAuthorize()
}