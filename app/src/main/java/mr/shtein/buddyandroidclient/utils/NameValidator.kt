package mr.shtein.buddyandroidclient.utils

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mr.shtein.data.exception.TooShortLengthException

class NameValidator(
    private val input: TextInputEditText,
    private val inputContainer: TextInputLayout
) : Validator {

    companion object {
        private const val INVALID_NAME_MSG: String = "Имя должно быть больше одного знака"
    }

    override fun validateValue(valueForValidate: String): Boolean {
        try {
            return nameChecker()
        } catch (error: TooShortLengthException) {
            inputContainer.error = error.message
        }
        return false
    }

    fun nameChecker(): Boolean {
        val value = input.text.toString()
        return assertIsValidName(value)
    }

    private fun assertIsValidName(value: String): Boolean {
        val valueLength = value.length
        if (valueLength < 2 ) throw TooShortLengthException(INVALID_NAME_MSG)
        return true
    }
}