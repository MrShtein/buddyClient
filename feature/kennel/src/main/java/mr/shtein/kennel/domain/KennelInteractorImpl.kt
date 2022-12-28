package mr.shtein.kennel.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.EmptyFieldException
import mr.shtein.data.exception.IllegalEmailException
import mr.shtein.data.exception.ValidationException
import mr.shtein.data.model.KennelPreview
import mr.shtein.data.repository.KennelRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.presentation.state.add_kennel.AddKennelState
import mr.shtein.kennel.util.mapper.KennelPreviewMapper
import mr.shtein.util.validator.IdentificationNumberValidator
import mr.shtein.util.validator.Validator

class KennelInteractorImpl(
    private val userPropertiesRepository: UserPropertiesRepository,
    private val networkKennelRepository: KennelRepository,
    private val kennelMapper: KennelPreviewMapper,
    private val emailValidator: Validator,
    private val emptyFieldValidator: Validator,
    private val phoneNumberValidator: Validator,
    private val identificationNumberValidator: Validator,
    private val dispatcher: CoroutineDispatcher
) : KennelInteractor {

    override suspend fun loadKennelsListByPersonId(): AddKennelState {
        val personId = userPropertiesRepository.getUserId()
        val token = userPropertiesRepository.getUserToken()
        val kennelResponsePreview = networkKennelRepository.getKennelsByPersonId(token, personId)
        if (kennelResponsePreview.isEmpty()) return AddKennelState.NoItem
        val kennelPreviewList: List<KennelPreview> =
            kennelMapper.transformFromDTOList(kennelResponsePreview)
        return AddKennelState.Success(kennelsList = kennelPreviewList)
    }

    override suspend fun validateEmail(email: String): ValidationResult =
        withContext(dispatcher) {
            try {
                emptyFieldValidator.validateValue(valueForValidate = email)
                emailValidator.validateValue(valueForValidate = email)
                return@withContext ValidationResult.Success
            } catch (ex: IllegalEmailException) {
                return@withContext ValidationResult.Failure(ex.message!!)
            } catch (ex: EmptyFieldException) {
                return@withContext ValidationResult.Failure(ex.message!!)
            }
        }

    override suspend fun validateKennelName(name: String): ValidationResult =
        withContext(dispatcher) {
            try {
                emptyFieldValidator.validateValue(valueForValidate = name)
                return@withContext ValidationResult.Success
            } catch (ex: EmptyFieldException) {
                return@withContext ValidationResult.Failure(ex.message!!)
            }
        }

    override suspend fun validatePhoneNumberLength(phone: String): ValidationResult =
        withContext(dispatcher) {
            try {
                phoneNumberValidator.validateValue(valueForValidate = phone)
                return@withContext ValidationResult.Success
            } catch (ex: ValidationException) {
                return@withContext ValidationResult.Failure(ex.message!!)
            }
        }

    override suspend fun validateStreet(street: String): ValidationResult =
        withContext(dispatcher) {
            try {
                emptyFieldValidator.validateValue(valueForValidate = street)
                return@withContext ValidationResult.Success
            } catch (ex: EmptyFieldException) {
                return@withContext ValidationResult.Failure(ex.message!!)
            }
        }

    override suspend fun validateHouseNum(houseNum: String): ValidationResult =
        withContext(dispatcher) {
            try {
                emptyFieldValidator.validateValue(valueForValidate = houseNum)
                return@withContext ValidationResult.Success
            } catch (ex: EmptyFieldException) {
                return@withContext ValidationResult.Failure(ex.message!!)
            }
        }

    override suspend fun validateIdentificationNum(identificationNum: String): ValidationResult  =
        withContext(dispatcher) {
            try {
                identificationNumberValidator.validateValue(valueForValidate = identificationNum)
                return@withContext ValidationResult.Success
            } catch (ex: ValidationException) {
                return@withContext ValidationResult.Failure(ex.message!!)
            }
        }
}
