package mr.shtein.buddyandroidclient.network.callback

import java.lang.Exception

interface PasswordCallBack {
    fun onSuccess()
    fun onFail(error: String)
    fun onFailure()
    fun onNoAuthorize()
}