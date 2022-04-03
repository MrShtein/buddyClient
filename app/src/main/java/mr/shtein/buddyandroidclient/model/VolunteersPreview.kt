package mr.shtein.buddyandroidclient.model

data class VolunteersPreview(
    val personId: Int,
    val volunteersAmount: Int,
    val animalsAmount: Int,
    val name: String,
    val avatarUrl: String,
    val isValid: Boolean
)
