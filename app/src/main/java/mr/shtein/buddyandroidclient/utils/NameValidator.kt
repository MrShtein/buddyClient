package mr.shtein.buddyandroidclient.utils

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mr.shtein.buddyandroidclient.exceptions.validate.TooShortLengthException

class NameValidator: Validator() {

    companion object {
        private const val INVALID_NAME_MSG: String = "Имя должно быть больше одного знака"
        private const val NO_INTERNET_MSG = "К сожалению интернет не работает"
    }

    fun nameChecker(input: TextInputEditText, inputContainer: TextInputLayout) {
        try {
            val value = input.text.toString()
            assertIsValidName(value)
        } catch (error: TooShortLengthException) {
            inputContainer.error = error.message
        }
    }

    private fun assertIsValidName(value: String): Boolean {
        if (value.length < 2) throw TooShortLengthException(INVALID_NAME_MSG)
        return true
    }
}