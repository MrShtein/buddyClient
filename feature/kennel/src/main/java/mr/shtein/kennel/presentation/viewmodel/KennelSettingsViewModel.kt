package mr.shtein.kennel.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mr.shtein.kennel.CityField
import mr.shtein.kennel.domain.KennelInteractor
import mr.shtein.kennel.domain.ValidationResult
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.kennel_settings.*

class KennelSettingsViewModel(
    val navigator: KennelNavigation,
    private val kennelInteractor: KennelInteractor
) : ViewModel() {

    companion object {
        private const val DEFAULT_INPUT_VALUE = ""
        private const val TAG = "KennelSettingsFragment"
        private val EMPTY_CITY_VALUE = CityField()
    }

    private val _cityFieldState: MutableLiveData<CityFieldState> =
        MutableLiveData(CityFieldState(CityField()))
    val cityFieldState: LiveData<CityFieldState> = _cityFieldState

    private val _emailFieldState: MutableLiveData<EmailFieldState> =
        MutableLiveData(EmailFieldState())
    val emailFieldState: LiveData<EmailFieldState> = _emailFieldState

    private val _houseNumberState: MutableLiveData<HouseNumberState> =
        MutableLiveData(HouseNumberState())
    val houseNumberState: LiveData<HouseNumberState> = _houseNumberState

    private val _identificationNumberState: MutableLiveData<IdentificationNumberState> =
        MutableLiveData(IdentificationNumberState())
    val identificationNumberState: LiveData<IdentificationNumberState> = _identificationNumberState

    private val _kennelAvatarState: MutableLiveData<KennelAvatarState> = MutableLiveData()
    val kennelAvatarState: LiveData<KennelAvatarState> = _kennelAvatarState

    private val _kennelNameState: MutableLiveData<KennelNameState> =
        MutableLiveData(KennelNameState())
    val kennelNameState: LiveData<KennelNameState> = _kennelNameState

    private val _phoneNumberState: MutableLiveData<PhoneNumberState> =
        MutableLiveData(PhoneNumberState())
    val phoneNumberState: LiveData<PhoneNumberState> = _phoneNumberState

    private val _streetState: MutableLiveData<StreetState> = MutableLiveData(StreetState())
    val streetState: LiveData<StreetState> = _streetState

    init {
        _kennelAvatarState.value = KennelAvatarState.EmptyValue
    }

    fun onCityChanged(city: String) {
        val (cityId, cityName, regionName) = city.split(",")
        val cityField = CityField(id = cityId.toLong(), fullName = "$cityName, $regionName")
        _cityFieldState.value?.field = cityField
        _cityFieldState.value?.validationState = ValidationState.Valid
    }

    fun onAvatarChanged(avatarPath: String, avatarUri: String) {
        _kennelAvatarState.value = KennelAvatarState.Value(
            pathToAvtStorage = avatarPath,
            avatarUri = avatarUri
        )
    }

    fun onAvatarCancelBtnClicked() {
        _kennelAvatarState.value = KennelAvatarState.EmptyValue
    }

    fun onCityInputFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            _cityFieldState.value?.field = EMPTY_CITY_VALUE
            _cityFieldState.value?.validationState = null
            navigator.moveToCityChoiceFromKennelSettings()
        }
    }

    fun onEmailInputFocusChanged(hasFocus: Boolean, email: String) {
        if (hasFocus) {
            _emailFieldState.value =
                _emailFieldState.value?.copy(email = email, validationState = null)
        } else {
            viewModelScope.launch {
                val result = kennelInteractor.validateEmail(email = email)
                when (result) {
                    ValidationResult.Success -> {
                        _emailFieldState.value = _emailFieldState.value?.copy(
                            email = email, validationState = ValidationState.Valid
                        )
                    }
                    is ValidationResult.Failure -> {
                        _emailFieldState.value?.email = email
                        _emailFieldState.value = _emailFieldState.value?.copy(
                            validationState = ValidationState.Invalid(
                                message = result.errorMessage
                            )
                        )
                    }
                }
            }
        }
    }

    fun onKennelNameFocusChanged(hasFocus: Boolean, name: String) {
        if (hasFocus) {
            _kennelNameState.value = _kennelNameState.value?.copy(
                validationState = null
            )
        } else {
            viewModelScope.launch {
                val result = kennelInteractor.validateKennelName(name = name)
                when (result) {
                    ValidationResult.Success -> {
                        _kennelNameState.value = _kennelNameState.value?.copy(
                            kennelName = name, validationState = ValidationState.Valid
                        )
                    }
                    is ValidationResult.Failure -> {
                        _kennelNameState.value = _kennelNameState.value?.copy(
                            kennelName = name, validationState = ValidationState.Invalid(
                                message = result.errorMessage
                            )
                        )
                    }
                }
            }
        }
    }

    fun onPhoneNumberFocusChanged(hasFocus: Boolean, phone: String) {
        if (hasFocus) {
            _phoneNumberState.value = _phoneNumberState.value?.copy(
                validationState = null
            )
        } else {
            viewModelScope.launch {
                val result = kennelInteractor.validatePhoneNumberLength(phone)
                when (result) {
                    ValidationResult.Success -> {
                        _phoneNumberState.value = _phoneNumberState.value?.copy(
                            phoneNum = phone, validationState = ValidationState.Valid
                        )
                    }
                    is ValidationResult.Failure -> {
                        _phoneNumberState.value = _phoneNumberState.value?.copy(
                            phoneNum = phone, validationState = ValidationState.Invalid(
                                message = result.errorMessage
                            )
                        )
                    }
                }
            }
        }
    }

    fun onStreetFocusChanged(hasFocus: Boolean, street: String) {
        if (hasFocus) {
            _streetState.value = _streetState.value?.copy(validationState = null)
        } else {
            viewModelScope.launch {
                val result = kennelInteractor.validateStreet(street)
                when (result) {
                    ValidationResult.Success -> {
                        _streetState.value = _streetState.value?.copy(
                            streetName = street, validationState = ValidationState.Valid
                        )
                    }
                    is ValidationResult.Failure -> {
                        _streetState.value = _streetState.value?.copy(
                            streetName = street, validationState = ValidationState.Invalid(
                                message = result.errorMessage
                            )
                        )
                    }
                }
            }
        }
    }

    fun onHouseFocusChanged(hasFocus: Boolean, house: String) {
        if (hasFocus) {
            _houseNumberState.value = _houseNumberState.value?.copy(
                validationState = null
            )
        } else {
            viewModelScope.launch {
                val result = kennelInteractor.validateHouseNum(house)
                when (result) {
                    ValidationResult.Success -> {
                        _houseNumberState.value = _houseNumberState.value?.copy(
                            houseNum = house, validationState = ValidationState.Valid
                        )
                    }
                    is ValidationResult.Failure -> {
                        _houseNumberState.value = _houseNumberState.value?.copy(
                            houseNum = house, validationState = ValidationState.Invalid(
                                message = result.errorMessage
                            )
                        )
                    }
                }
            }
        }
    }

    fun onIdentificationNumFocusChanged(hasFocus: Boolean, identificationNum: String) {
        if (hasFocus) {
            _identificationNumberState.value = _identificationNumberState.value?.copy(
                validationState = null
            )
        } else {
            viewModelScope.launch {
                val result = kennelInteractor.validateIdentificationNum(identificationNum)
                when (result) {
                    ValidationResult.Success -> {
                        _identificationNumberState.value = _identificationNumberState.value?.copy(
                            identificationNum = identificationNum,
                            validationState = ValidationState.Valid
                        )
                    }
                    is ValidationResult.Failure -> {
                        _identificationNumberState.value = _identificationNumberState.value?.copy(
                            identificationNum = identificationNum,
                            validationState = ValidationState.Invalid(message = result.errorMessage)
                        )
                    }
                }
            }
        }
    }


}