package mr.shtein.util.di

import mr.shtein.model.volunteer.VolunteersBid
import mr.shtein.util.state.VolunteerBidsState

interface KennelAdministratorInteractor {
    suspend fun getAllKennelsVolunteerBids(): VolunteerBidsState<Map<Int, List<VolunteersBid>>>
}