package mr.shtein.buddyandroidclient.utils

import android.util.Log
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.exceptions.validate.OldPasswordsIsNotValidException
import mr.shtein.buddyandroidclient.exceptions.validate.PasswordsIsDifferentException
import mr.shtein.buddyandroidclient.exceptions.validate.TooShortLengthException
import mr.shtein.buddyandroidclient.model.PasswordCheckRequest
import mr.shtein.buddyandroidclient.retrofit.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordValidator(): Validator() {
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
                                 token: String, oldPwdCallBack: (error: String) -> Unit) {
        val retrofitService = Common.retrofitService
        var headerMap = hashMapOf<String, String>()
        headerMap["Authorization"] = "Bearer $token"
        val passwordRequest = PasswordCheckRequest(password, personId)

        retrofitService.checkOldPassword(headerMap, passwordRequest)
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.body() == false) {
                        oldPwdCallBack(OLD_PASSWORD_IS_WRONG)
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.e("INTERNET ERROR", "Нет сети")
                }
            })

    }
}