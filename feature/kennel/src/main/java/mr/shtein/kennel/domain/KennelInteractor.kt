package mr.shtein.kennel.domain

import mr.shtein.data.model.AvatarWrapper
import mr.shtein.data.model.KennelRequest
import mr.shtein.kennel.presentation.state.add_kennel.AddKennelState
import mr.shtein.kennel.presentation.state.kennel_confirm.NewKennelSendingState
import mr.shtein.kennel.presentation.state.kennel_home.AnimalListState
import mr.shtein.model.volunteer.VolunteersBid
import mr.shtein.util.state.VolunteerBidsState

interface KennelInteractor {
    suspend fun loadKennelsListByPersonId(): AddKennelState
    suspend fun getKennelAvatar(avatarUri: String): AvatarWrapper?
    suspend fun addNewKennel(avatarWrapper: AvatarWrapper?, kennelRequest: KennelRequest): NewKennelSendingState
    suspend fun validateEmail(email: String): ValidationResult
    suspend fun validateKennelName(name: String): ValidationResult
    suspend fun validatePhoneNumberLength(phone: String): ValidationResult
    suspend fun validateStreet(street: String): ValidationResult
    suspend fun validateHouseNum(houseNum: String): ValidationResult
    suspend fun validateIdentificationNum(identificationNum: String): ValidationResult
    suspend fun getAnimalByTypeIdAndKennelId(animalType: String, kennelId: Int): AnimalListState
    suspend fun getVolunteerBids(kennelId: Int): VolunteerBidsState<List<VolunteersBid>>
}