package mr.shtein.kennel.presentation.adapter

import mr.shtein.model.volunteer.VolunteerDTO
import mr.shtein.model.volunteer.VolunteersBid

sealed class VolunteerAndBidItems {
    data class BidHeader(val bidsCount: Int) : VolunteerAndBidItems()
    data class BidBody(val bidBody: VolunteersBid) : VolunteerAndBidItems()
    data class VolunteerHeader(val volunteersCount: Int) : VolunteerAndBidItems()
    data class VolunteerBody(val volunteerBody: VolunteerDTO) : VolunteerAndBidItems()
}
