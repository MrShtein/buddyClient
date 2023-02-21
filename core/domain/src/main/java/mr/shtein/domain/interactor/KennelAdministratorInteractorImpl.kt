package mr.shtein.domain.interactor

import mr.shtein.data.repository.*
import mr.shtein.util.state.VolunteerBidsState
import mr.shtein.model.volunteer.VolunteersBid
import mr.shtein.util.di.KennelAdministratorInteractor

class KennelAdministratorInteractorImpl(
    private val userPropertiesRepository: UserPropertiesRepository,
    private val administratorRepository: KennelAdministratorRepository
) : KennelAdministratorInteractor {

    override suspend fun getAllKennelsVolunteerBids(): VolunteerBidsState<Map<Int, List<VolunteersBid>>> {
        val token: String = userPropertiesRepository.getUserToken()
        val bidList: List<VolunteersBid>  = administratorRepository.getAllKennelsVolunteerBids(
            token = token
        )
        return VolunteerBidsState.Success(
            mapVolunteerBidsToHashMapByKennelId(bidList)
        )
    }

    private fun mapVolunteerBidsToHashMapByKennelId(
        volunteerBidList: List<VolunteersBid>
    ) : Map<Int, List<VolunteersBid>> {
        return volunteerBidList.groupBy() {
            it.kennelId
        }
    }

}
