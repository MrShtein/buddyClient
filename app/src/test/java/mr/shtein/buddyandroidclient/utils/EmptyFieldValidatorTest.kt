package mr.shtein.buddyandroidclient.utils

import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.utils.EmptyFieldValidator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmptyFieldValidatorTest {

    val emptyFieldValidator: EmptyFieldValidator = EmptyFieldValidator()
    val EMPTY_FIELD_MSG: String = "Данное поле не может быть пустым"

    @Test
    fun isValidValue() {
        val value = "abc"
        val result = emptyFieldValidator.validateValue(value)
        assertTrue(result)
    }

    @Test
    fun isNotValidEmptyField() {
        val exception: EmptyFieldException = Assertions.assertThrows(
            EmptyFieldException::class.java
        ) {
            val value = ""
            emptyFieldValidator.validateValue(value)
        }
        Assertions.assertEquals(EMPTY_FIELD_MSG, exception.message)
    }

    @Test
    fun isNotValidSpacesInText() {
        val exception: EmptyFieldException = Assertions.assertThrows(
            EmptyFieldException::class.java
        ) {
            val value = "    "
            emptyFieldValidator.validateValue(value)
        }
        Assertions.assertEquals(EMPTY_FIELD_MSG, exception.message)
    }

}