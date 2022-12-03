package mr.shtein.util.validator

interface Validator {
    fun validateValue(valueForValidate: String): Boolean
}