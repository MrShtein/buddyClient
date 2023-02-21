package mr.shtein.util.state

import mr.shtein.model.volunteer.VolunteersBid

data class BottomNavState(
    val isKennelBadgeVisible: Boolean,
    var volunteerBidsState: VolunteerBidsState<Map<Int, List<VolunteersBid>>>
)
