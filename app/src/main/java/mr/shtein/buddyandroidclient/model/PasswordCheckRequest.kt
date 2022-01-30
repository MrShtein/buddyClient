package mr.shtein.buddyandroidclient.model

data class PasswordCheckRequest(
    val password: String,
    val personId: Long
)
