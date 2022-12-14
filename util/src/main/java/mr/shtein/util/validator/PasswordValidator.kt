package mr.shtein.util.validator

import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mr.shtein.data.exception.EmptyFieldException
import mr.shtein.data.exception.PasswordsIsDifferentException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.exception.TooShortLengthException
import mr.shtein.data.repository.UserRepository
import mr.shtein.model.PasswordCheckRequest
import java.net.ConnectException
import java.net.SocketTimeoutException

class PasswordValidator(
    private val userRepository: UserRepository,
    val coroutineScope: CoroutineScope
) : EmptyFieldValidator() {

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

    fun assertIsValidOldPassword(
        password: String, personId: Long,
        token: String, oldPwdCallBack: PasswordCallBack
    ) {
        val headerMap = hashMapOf<String, String>()
        headerMap["Authorization"] = token
        val passwordRequest = PasswordCheckRequest(password, personId)

        coroutineScope.launch {
            try {
                val result = userRepository.checkOldPassword(
                    headerMap = headerMap,
                    passwordCheckRequest = passwordRequest
                )
                if (result) {
                    oldPwdCallBack.onSuccess()
                    return@launch
                }
                oldPwdCallBack.onFail(OLD_PASSWORD_IS_WRONG)
            } catch (error: ServerErrorException) {
                oldPwdCallBack.onFailure()
            } catch (error: ConnectException) {
                oldPwdCallBack.onFailure()
            } catch (error: SocketTimeoutException) {
                oldPwdCallBack.onFailure()
            }
        }
    }
}