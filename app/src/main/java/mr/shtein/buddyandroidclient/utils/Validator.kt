package mr.shtein.buddyandroidclient.utils

import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException

open class Validator {

    companion object {
        const val EMPTY_FIELD_MSG: String = "Необходимо заполнить поле"
    }

    protected fun assertIsNotEmptyField(value: String): Boolean {
        if (value.isBlank()) {
            throw EmptyFieldException(EMPTY_FIELD_MSG)
        }
        return true
    }




}