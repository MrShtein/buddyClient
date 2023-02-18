package mr.shtein.kennel.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import mr.shtein.data.exception.*
import mr.shtein.data.mapper.AnimalMapper
import mr.shtein.data.model.Animal
import mr.shtein.data.model.AvatarWrapper
import mr.shtein.data.model.KennelPreview
import mr.shtein.data.model.KennelRequest
import mr.shtein.data.repository.AnimalRepository
import mr.shtein.data.repository.KennelRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.presentation.state.add_kennel.AddKennelState
import mr.shtein.kennel.presentation.state.kennel_confirm.NewKennelSendingState
import mr.shtein.kennel.presentation.state.kennel_home.AnimalListState
import mr.shtein.util.state.VolunteerBidsState
import mr.shtein.kennel.util.mapper.KennelPreviewMapper
import mr.shtein.util.validator.Validator

class KennelInteractorImpl(
    private val userPropertiesRepository: UserPropertiesRepository,
    private val networkKennelRepository: KennelRepository,
    private val animalRepository: AnimalRepository,
    private val animalMapper: AnimalMapper,
    private val kennelMapper: KennelPreviewMapper,
    private val emailValidator: Validator,
    private val emptyFieldValidator: Validator,
    private val phoneNumberValidator: Validator,
    private val identificationNumberValidator: Validator,
    private val dispatcher: CoroutineDispatcher
) : KennelInteractor {

    companion object {
        //Совсем без текста в элементе не отображается ошибка, цвет текста в данном случае - transparent
        private const val HOME_ERROR_MESSAGE: String = "1"
    }

    override suspend fun loadKennelsListByPersonId(): AddKennelState {
        val personId = userPropertiesRepository.getUserId()
        val token = userPropertiesRepository.getUserToken()
        val kennelResponsePreview = networkKennelRepository.getKennelsByPersonId(token, personId)
        if (kennelResponsePreview.isEmpty()) return AddKennelState.NoItem
        val kennelPreviewList: List<KennelPreview> =
            kennelMapper.transformFromDTOList(kennelResponsePreview)
        return AddKennelState.Success(kennelsList = kennelPreviewList)
    }

    override suspend fun getKennelAvatar(avatarUri: String): AvatarWrapper? {
        return networkKennelRepository.getKennelAvatar(avatarUri = avatarUri)
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
                return@withContext ValidationResult.Failure(HOME_ERROR_MESSAGE)
            }
        }

    override suspend fun validateIdentificationNum(identificationNum: String): ValidationResult =
        withContext(dispatcher) {
            try {
                identificationNumberValidator.validateValue(valueForValidate = identificationNum)
                return@withContext ValidationResult.Success
            } catch (ex: ValidationException) {
                return@withContext ValidationResult.Failure(ex.message!!)
            }
        }

    override suspend fun addNewKennel(
        avatarWrapper: AvatarWrapper?,
        kennelRequest: KennelRequest
    ): NewKennelSendingState = withContext(dispatcher) {
        try {
            val token = userPropertiesRepository.getUserToken()
            networkKennelRepository.addNewKennel(
                token = token, kennelRequest = kennelRequest, avatarWrapper = avatarWrapper
            )
            return@withContext NewKennelSendingState.Success
        } catch (ex: ItemAlreadyExistException) {
            return@withContext NewKennelSendingState.Exist
        }
    }

    override suspend fun getAnimalByTypeIdAndKennelId(
        animalType: String,
        kennelId: Int
    ): AnimalListState = withContext(dispatcher) {
        val token = userPropertiesRepository.getUserToken()
        val animalDTOList = animalRepository.getAnimalsByKennelIdAndAnimalType(
            token = token, kennelId = kennelId, animalType = animalType
        )
        val animalList: MutableList<Animal> =
            animalMapper.transformFromDTOList(animalDTOList = animalDTOList).toMutableList()
        return@withContext AnimalListState.Success(animalList = animalList)
    }

    override suspend fun getVolunteerBids(kennelId: Int): VolunteerBidsState =
        withContext(dispatcher) {
            try {
                val token = userPropertiesRepository.getUserToken()
                return@withContext VolunteerBidsState.Success(
                    networkKennelRepository.getVolunteerBids(token = token, kennelId = kennelId)
                )
            } catch (ex: NoAuthorizationException) {
                return@withContext VolunteerBidsState.Failure(R.string.no_permission_exception_text)
            } catch (ex: ServerErrorException) {
                return@withContext VolunteerBidsState.Failure(R.string.server_error_msg)
            }
        }
}
