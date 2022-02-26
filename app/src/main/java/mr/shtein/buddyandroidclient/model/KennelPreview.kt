package mr.shtein.buddyandroidclient.model

data class KennelPreview(
    val kennelId: Int,
    val volunteersAmount: Int,
    val animalsAmount: Int,
    val name: String,
    val avatarUrl: String,
    val isValid: Boolean
)
