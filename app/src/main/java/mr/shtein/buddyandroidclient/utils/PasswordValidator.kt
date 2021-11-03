package mr.shtein.buddyandroidclient.utils

import com.google.android.material.textfield.TextInputEditText
import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.exceptions.validate.PasswordsIsDifferentException
import mr.shtein.buddyandroidclient.exceptions.validate.TooShortLengthException

class PasswordValidator(): Validator() {
    companion object {
        private const val TOO_SHORT_PASSWORD_MSG: String = "Пароль слишком короткий"
        private const val PASSWORD_IS_DIFFERENT_MSG: String = "Пароли не совпадают"
    }

    public fun assertIsValidPassword(value: String) {
        if (value.isEmpty()) throw EmptyFieldException(EMPTY_FIELD_MSG)
        if (value.length <= 5) throw TooShortLengthException(TOO_SHORT_PASSWORD_MSG)
    }

    public fun assertIsValidRepeatPassword(value: String, passwordInput: TextInputEditText) {
        if (value.isEmpty()) throw EmptyFieldException(EMPTY_FIELD_MSG)
        val password: String = passwordInput.text.toString()
        if (password != value) throw PasswordsIsDifferentException(PASSWORD_IS_DIFFERENT_MSG)
    }
}