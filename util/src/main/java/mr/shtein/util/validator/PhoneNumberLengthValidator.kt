package mr.shtein.util.validator

import mr.shtein.data.exception.ValidationException

class PhoneNumberLengthValidator : Validator {

    companion object {
        private const val SHORT_PHONE_NUMBER_EXCEPTION = "Вы ввели слишком короткий номер"
    }

    override fun validateValue(valueForValidate: String): Boolean {
        return isValidPhoneNumLength(valueForValidate)
    }

    private fun isValidPhoneNumLength(phoneNum: String): Boolean {
        if (phoneNum.length < 18) throw ValidationException(SHORT_PHONE_NUMBER_EXCEPTION)
        return true
    }




}