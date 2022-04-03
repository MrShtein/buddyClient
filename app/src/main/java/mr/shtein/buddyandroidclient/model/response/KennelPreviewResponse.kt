package mr.shtein.buddyandroidclient.model.response

data class KennelPreviewResponse(
    val kennelId: Int,
    val volunteersAmount: Int,
    val animalsAmount: Int,
    val name: String,
    val avatarUrl: String,
    val isValid: Boolean
)
