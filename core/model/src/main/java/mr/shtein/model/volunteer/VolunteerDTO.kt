package mr.shtein.model.volunteer

data class VolunteerDTO  (
    val avatarUrl: String,
    val name: String,
    val surname: String,
    val personId: Long,
    val volunteerId: Long,
    val phoneNumber: String,
    val volunteerPermissions: List<VolunteerPermission>
) : VolunteerBids