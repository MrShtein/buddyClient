package mr.shtein.model.volunteer

data class VolunteersBid (
    val avatarUrl: String,
    val name: String,
    val surname: String,
    val personId: Long,
    val bidId: Long
) : Sharelable