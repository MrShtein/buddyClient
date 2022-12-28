package mr.shtein.kennel.domain

import mr.shtein.kennel.presentation.state.add_kennel.AddKennelState

interface KennelInteractor {
    suspend fun loadKennelsListByPersonId(): AddKennelState
    suspend fun validateEmail(email: String): ValidationResult
    suspend fun validateKennelName(name: String): ValidationResult
    suspend fun validatePhoneNumberLength(phone: String): ValidationResult
    suspend fun validateStreet(street: String): ValidationResult
    suspend fun validateHouseNum(houseNum: String): ValidationResult
    suspend fun validateIdentificationNum(identificationNum: String): ValidationResult
}