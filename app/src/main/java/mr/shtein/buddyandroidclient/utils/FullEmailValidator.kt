package mr.shtein.buddyandroidclient.utils

import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mr.shtein.buddyandroidclient.network.callback.MailCallback
import mr.shtein.buddyandroidclient.exceptions.validate.*
import mr.shtein.buddyandroidclient.model.response.EmailCheckRequest
import mr.shtein.buddyandroidclient.retrofit.RetrofitService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FullEmailValidator (
   val emailCheckRequest: EmailCheckRequest,
   val emailCallback: MailCallback,
   val dialog: AlertDialog?
    ): Validator, KoinComponent, EmptyFieldValidator() {

    override fun validateValue(email: String): Boolean {
        return assertIsValidEmail(email)
    }

    val retrofitService: RetrofitService by inject()

    companion object {
         const val INVALID_EMAIL_MSG: String = "Вы ввели некорректный email"
         const val EXISTED_EMAIL_MSG: String = "Пользователь с таким email уже существует"
         const val NO_INTERNET_MSG = "К сожалению интернет не работает"
         const val NO_AUTHORIZE_MSG = "Ошибка авторизации"


        fun assertIsValidEmail(value: String): Boolean {
            val regexForEmail: Regex =
                Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
            if (!regexForEmail.matches(value)) throw IllegalEmailException(Companion.INVALID_EMAIL_MSG)
            return true
        }

    }

    fun emailChecker(input: TextInputEditText, inputContainer: TextInputLayout) {
        try {
            val value = input.text.toString()
            assertIsNotEmptyField(value)
            assertIsValidEmail(value)
            isEmailExists(emailCheckRequest, emailCallback)
        } catch (error: EmptyFieldException) {
            dialog?.dismiss()
            inputContainer.error = error.message
        } catch (error: IllegalEmailException) {
            dialog?.dismiss()
            inputContainer.error = error.message
        } catch (error: ExistedEmailException) {
            dialog?.dismiss()
            inputContainer.error = error.message
        } catch (error: TooShortLengthException) {
            dialog?.dismiss()
            inputContainer.error = error.message
        }
    }



    private fun assertIsEmailAlreadyExists(value: Boolean): Boolean {
        if (value) throw ExistedEmailException(EXISTED_EMAIL_MSG)
        return false
    }

    private fun isEmailExists(emailCheckRequest: EmailCheckRequest, callback: MailCallback) {
        retrofitService.isEmailExists(emailCheckRequest)
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    val value: Boolean? = response.body()
                    try {
                        if  (response.raw().code() == 403) throw NoAuthorizationException("Вы не авторизованы") //TODO Решить вопрос с обработкой этой ошибки
                        assertIsEmailAlreadyExists(value ?: false)
                        callback.onSuccess()
                    } catch (error: ExistedEmailException) {
                        callback.onFail(error)
                    } catch (error: NoAuthorizationException) {
                        callback.onNoAuthorize()
                    }

                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    callback.onFailure()
                }
            })
    }


}