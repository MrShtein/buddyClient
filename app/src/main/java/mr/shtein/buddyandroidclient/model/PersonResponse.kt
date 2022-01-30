package mr.shtein.buddyandroidclient.model

data class PersonResponse(
    val isUpgrade: Boolean,
    val ok: Boolean,
    val error: ErrorResponse?
)
