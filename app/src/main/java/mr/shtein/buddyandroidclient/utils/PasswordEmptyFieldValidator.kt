package mr.shtein.buddyandroidclient.utils

import com.google.android.material.textfield.TextInputEditText
import mr.shtein.buddyandroidclient.exceptions.validate.*
import mr.shtein.buddyandroidclient.model.PasswordCheckRequest
import mr.shtein.buddyandroidclient.network.callback.PasswordCallBack
import mr.shtein.buddyandroidclient.retrofit.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordEmptyFieldValidator(val networkService: NetworkService): EmptyFieldValidator() {

    companion object {
        private const val TOO_SHORT_PASSWORD_MSG: String = "Пароль слишком короткий"
        private const val PASSWORD_IS_DIFFERENT_MSG: String = "Пароли не совпадают"
        private const val OLD_PASSWORD_IS_WRONG: String = "Старый пароль введен неверно"
    }

    fun assertIsValidPassword(value: String) {
        if (value.isEmpty()) throw EmptyFieldException(EMPTY_FIELD_MSG)
        if (value.length <= 5) throw TooShortLengthException(TOO_SHORT_PASSWORD_MSG)
    }

    fun assertIsValidRepeatPassword(value: String, passwordInput: TextInputEditText) {
        if (value.isEmpty()) throw EmptyFieldException(EMPTY_FIELD_MSG)
        val password: String = passwordInput.text.toString()
        if (password != value) throw PasswordsIsDifferentException(PASSWORD_IS_DIFFERENT_MSG)
    }

    fun assertIsValidOldPassword(password: String, personId: Long,
                                 token: String, oldPwdCallBack: PasswordCallBack) {
        var headerMap = hashMapOf<String, String>()
        headerMap["Authorization"] = token
        val passwordRequest = PasswordCheckRequest(password, personId)

        networkService.checkOldPassword(headerMap, passwordRequest)
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    try {
                        if  (response.raw().code() == 403) throw NoAuthorizationException("Вы не авторизованы")
                        val responseBody = response.body()
                        if (responseBody == true) {
                            oldPwdCallBack.onSuccess()
                        } else if (responseBody == false) {
                            oldPwdCallBack.onFail(OLD_PASSWORD_IS_WRONG)
                        }
                    } catch (error: NoAuthorizationException) {
                        oldPwdCallBack.onNoAuthorize()
                    }

                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    oldPwdCallBack.onFailure()
                }
            })

    }
}