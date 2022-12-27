package mr.shtein.kennel.domain

sealed class ValidationResult {
    object Success: ValidationResult()
    data class Failure(val errorMessage: String): ValidationResult()
}
