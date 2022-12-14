package mr.shtein.model

data class PasswordCheckRequest(
    val password: String,
    val personId: Long
)
