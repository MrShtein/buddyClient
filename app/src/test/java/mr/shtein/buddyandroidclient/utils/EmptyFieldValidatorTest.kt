package mr.shtein.buddyandroidclient.utils

import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.utils.EmptyFieldValidator
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EmptyFieldValidatorTest {

    val emptyFieldValidator: EmptyFieldValidator = EmptyFieldValidator()

    @Test
    fun isValidValue() {
        val value = "abc"
        val result = emptyFieldValidator.validateValue(value)
        Assert.assertTrue(result)
    }

    @Test(expected = EmptyFieldException::class)
    fun isNotValidEmptyField() {
        val value = ""
        val result = emptyFieldValidator.validateValue(value)
    }

    @Test(expected = EmptyFieldException::class)
    fun isNotValidSpacesInText() {
        val value = "     "
        val result = emptyFieldValidator.validateValue(value)
    }

}