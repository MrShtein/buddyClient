package mr.shtein.kennel.presentation.viewmodel

import android.util.Log
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

    private val _houseNumberState: MutableLiveData<HouseNumberState> = MutableLiveData()
    val houseNumberState: LiveData<HouseNumberState> = _houseNumberState

    private val _identificationNumberState: MutableLiveData<IdentificationNumberState> =
        MutableLiveData()
    val identificationNumberState: LiveData<IdentificationNumberState> = _identificationNumberState

    private val _kennelAvatarState: MutableLiveData<KennelAvatarState> = MutableLiveData()
    val kennelAvatarState: LiveData<KennelAvatarState> = _kennelAvatarState

    private val _organizationNameState: MutableLiveData<OrganizationNameState> = MutableLiveData()
    val organizationNameState: LiveData<OrganizationNameState> = _organizationNameState

    private val _phoneNumberState: MutableLiveData<PhoneNumberState> = MutableLiveData()
    val phoneNumberState: LiveData<PhoneNumberState> = _phoneNumberState

    private val _streetState: MutableLiveData<StreetState> = MutableLiveData()
    val streetState: LiveData<StreetState> = _streetState

    init {
        _houseNumberState.value = HouseNumberState.Value(value = DEFAULT_INPUT_VALUE)
        _identificationNumberState.value =
            IdentificationNumberState.Value(value = DEFAULT_INPUT_VALUE)
        _kennelAvatarState.value = KennelAvatarState.EmptyValue
        _organizationNameState.value = OrganizationNameState.Value(value = DEFAULT_INPUT_VALUE)
        _phoneNumberState.value = PhoneNumberState.Value(value = DEFAULT_INPUT_VALUE)
        _streetState.value = StreetState.Value(value = DEFAULT_INPUT_VALUE)
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
        if (hasFocus && _organizationNameState.value is OrganizationNameState.Error) {
            _organizationNameState.value = OrganizationNameState.Value(value = name)
        } else if (!hasFocus) {
            viewModelScope.launch {
                val result = kennelInteractor.validateKennelName(name = name)
                when (result) {
                    ValidationResult.Success -> {
                        _organizationNameState.value = OrganizationNameState.Value(value = name)
                    }
                    is ValidationResult.Failure -> {
                        _organizationNameState.value = OrganizationNameState.Error(
                            message = result.errorMessage,
                            wrongValue = name
                        )
                    }
                }
            }
        }
    }

    fun onPhoneNumberFocusChanged(hasFocus: Boolean, phone: String) {
        if (hasFocus && _phoneNumberState.value is PhoneNumberState.Error) {
            _phoneNumberState.value = PhoneNumberState.Value(value = phone)
        } else if (!hasFocus) {
            viewModelScope.launch {
                val result = kennelInteractor.validatePhoneNumberLength(phone)
                when (result) {
                    ValidationResult.Success -> {
                        _phoneNumberState.value = PhoneNumberState.Value(value = phone)
                    }
                    is ValidationResult.Failure -> {
                        _phoneNumberState.value = PhoneNumberState.Error(
                            message = result.errorMessage,
                            wrongValue = phone
                        )
                    }
                }
            }
        }
    }

    fun onStreetFocusChanged(hasFocus: Boolean, street: String) {
        if (hasFocus && _streetState.value is StreetState.Error) {
            _streetState.value = StreetState.Value(value = street)
        } else if (!hasFocus) {
            viewModelScope.launch {
                val result = kennelInteractor.validateStreet(street)
                when (result) {
                    ValidationResult.Success -> {
                        _streetState.value = StreetState.Value(value = street)
                    }
                    is ValidationResult.Failure -> {
                        _streetState.value = StreetState.Error(
                            message = result.errorMessage,
                            wrongValue = street
                        )
                    }
                }
            }
        }
    }

    fun onHouseFocusChanged(hasFocus: Boolean, house: String) {
        if (hasFocus && _houseNumberState.value is HouseNumberState.Error) {
            _houseNumberState.value = HouseNumberState.Value(value = house)
        } else if (!hasFocus) {
            viewModelScope.launch {
                val result = kennelInteractor.validateHouseNum(house)
                when (result) {
                    ValidationResult.Success -> {
                        _houseNumberState.value = HouseNumberState.Value(value = house)
                    }
                    is ValidationResult.Failure -> {
                        _houseNumberState.value = HouseNumberState.Error(
                            message = result.errorMessage,
                            wrongValue = house
                        )
                    }
                }
            }
        }
    }

    fun onIdentificationNumFocusChanged(hasFocus: Boolean, identificationNum: String) {
        if (hasFocus && _identificationNumberState.value is IdentificationNumberState.Error) {
            _identificationNumberState.value =
                IdentificationNumberState.Value(value = identificationNum)
        } else if (!hasFocus) {
            viewModelScope.launch {
                val result = kennelInteractor.validateIdentificationNum(identificationNum)
                when (result) {
                    ValidationResult.Success -> {
                        _identificationNumberState.value =
                            IdentificationNumberState.Value(value = identificationNum)
                    }
                    is ValidationResult.Failure -> {
                        _identificationNumberState.value = IdentificationNumberState.Error(
                            message = result.errorMessage,
                            wrongValue = identificationNum
                        )
                    }
                }
            }
        }
    }


}