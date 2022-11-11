package mr.shtein.model

data class PersonResponse(
    val isUpgrade: Boolean,
    val newToken: String,
    val ok: Boolean,
    val error: ErrorResponse?
)
