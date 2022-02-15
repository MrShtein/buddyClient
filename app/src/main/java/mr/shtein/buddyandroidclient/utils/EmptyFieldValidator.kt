package mr.shtein.buddyandroidclient.utils

import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException

open class EmptyFieldValidator: Validator  {

    companion object {
        const val EMPTY_FIELD_MSG: String = "Данное поле не может быть пустым"
    }

    override fun validateValue(valueForValidate: String): Boolean {
        return assertIsNotEmptyField(valueForValidate)
    }

    fun assertIsNotEmptyField(value: String): Boolean {
        if (value.isBlank()) {
            throw EmptyFieldException(EMPTY_FIELD_MSG)
        }
        return true
    }






}