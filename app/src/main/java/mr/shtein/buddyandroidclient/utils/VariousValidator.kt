package mr.shtein.buddyandroidclient.utils

import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.exceptions.validate.ValidationException

class VariousValidator {

    companion object {

        private const val EMPTY_FIELD_EXCEPTION_MSG = "Данное поле не может быть пустым"
        private const val SHORT_PHONE_NUMBER_EXCEPTION = "Вы ввели слишком короткий номер"

        fun isEmptyField(anyText: String): Boolean {
            if (anyText == "") throw EmptyFieldException(EMPTY_FIELD_EXCEPTION_MSG)
            return true
        }

        fun isValidPhoneNum(phoneNum: String): Boolean {
            if (phoneNum.length < 13) throw ValidationException(SHORT_PHONE_NUMBER_EXCEPTION)
            return true
        }
    }


}