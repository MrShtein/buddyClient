package mr.shtein.model

data class EmailCheckRequest(
    val email: String,
    val personId: Long = 0
)
