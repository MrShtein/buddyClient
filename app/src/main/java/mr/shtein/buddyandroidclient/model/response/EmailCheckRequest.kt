package mr.shtein.buddyandroidclient.model.response

data class EmailCheckRequest(
    val email: String,
    val personId: Long = 0
)
