package mr.shtein.kennel.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mr.shtein.data.model.KennelRequest
import mr.shtein.kennel.CityField
import mr.shtein.kennel.domain.KennelInteractor
import mr.shtein.kennel.domain.ValidationResult
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.kennel_settings.BuildingState
import mr.shtein.kennel.presentation.state.kennel_settings.*

class KennelSettingsViewModel(
    val navigator: KennelNavigation,
    private val kennelInteractor: KennelInteractor
) : ViewModel() {

    companion object {
        private const val CITY_FIELD_ERROR = "Вы не указали город"
        private const val TAG = "KennelSettingsFragment"
        private val EMPTY_CITY_VALUE = CityField()
    }

    private var isSomethingValidatingNow = false

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

    private val _buildingState: MutableLiveData<BuildingState> = MutableLiveData(BuildingState())
    val buildingState: LiveData<BuildingState> = _buildingState

    private val _saveBtnState: MutableLiveData<SaveBtnState> = MutableLiveData(SaveBtnState())
    val saveBtnState: LiveData<SaveBtnState> = _saveBtnState

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
                isSomethingValidatingNow = true
                validateEmail(email)
                isSomethingValidatingNow = false
            }
        }
    }

    private suspend fun validateEmail(email: String): Boolean {
        val result = kennelInteractor.validateEmail(email = email)
        when (result) {
            ValidationResult.Success -> {
                _emailFieldState.value = _emailFieldState.value?.copy(
                    email = email, validationState = ValidationState.Valid
                )
                return true
            }
            is ValidationResult.Failure -> {
                _emailFieldState.value?.email = email
                _emailFieldState.value = _emailFieldState.value?.copy(
                    validationState = ValidationState.Invalid(
                        message = result.errorMessage
                    )
                )
                return false
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
                isSomethingValidatingNow = true
                validateKennelName(name)
                isSomethingValidatingNow = false
            }
        }
    }

    private suspend fun validateKennelName(name: String): Boolean {
        val result = kennelInteractor.validateKennelName(name = name)
        when (result) {
            ValidationResult.Success -> {
                _kennelNameState.value = _kennelNameState.value?.copy(
                    kennelName = name, validationState = ValidationState.Valid
                )
                return true
            }
            is ValidationResult.Failure -> {
                _kennelNameState.value = _kennelNameState.value?.copy(
                    kennelName = name, validationState = ValidationState.Invalid(
                        message = result.errorMessage
                    )
                )
                return false
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
                isSomethingValidatingNow = true
                validatePhoneNumber(phone)
                isSomethingValidatingNow = false
            }
        }
    }

    private suspend fun validatePhoneNumber(phone: String): Boolean {
        val result = kennelInteractor.validatePhoneNumberLength(phone)
        when (result) {
            ValidationResult.Success -> {
                _phoneNumberState.value = _phoneNumberState.value?.copy(
                    phoneNum = phone, validationState = ValidationState.Valid
                )
                return true
            }
            is ValidationResult.Failure -> {
                _phoneNumberState.value = _phoneNumberState.value?.copy(
                    phoneNum = phone, validationState = ValidationState.Invalid(
                        message = result.errorMessage
                    )
                )
                return false
            }
        }
    }

    fun onStreetFocusChanged(hasFocus: Boolean, street: String) {
        if (hasFocus) {
            _streetState.value = _streetState.value?.copy(validationState = null)
        } else {
            viewModelScope.launch {
                isSomethingValidatingNow = true
                validateStreetName(street)
                isSomethingValidatingNow = false
            }
        }
    }

    private suspend fun validateStreetName(street: String): Boolean {
        val result = kennelInteractor.validateStreet(street)
        when (result) {
            ValidationResult.Success -> {
                _streetState.value = _streetState.value?.copy(
                    streetName = street, validationState = ValidationState.Valid
                )
                return true
            }
            is ValidationResult.Failure -> {
                _streetState.value = _streetState.value?.copy(
                    streetName = street, validationState = ValidationState.Invalid(
                        message = result.errorMessage
                    )
                )
                return false
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
                isSomethingValidatingNow = true
                validateHouseNum(house)
                isSomethingValidatingNow = false
            }
        }
    }

    fun onBuildingFocusChanged(hasFocus: Boolean, building: String) {
        if (!hasFocus) _buildingState.value = _buildingState.value?.copy(building = building)
    }

    private suspend fun validateHouseNum(house: String): Boolean {
        val result = kennelInteractor.validateHouseNum(house)
        when (result) {
            ValidationResult.Success -> {
                _houseNumberState.value = _houseNumberState.value?.copy(
                    houseNum = house, validationState = ValidationState.Valid
                )
                return true
            }
            is ValidationResult.Failure -> {
                _houseNumberState.value = _houseNumberState.value?.copy(
                    houseNum = house, validationState = ValidationState.Invalid(
                        message = result.errorMessage
                    )
                )
                return false
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
                isSomethingValidatingNow = true
                validateIdentificationNum(identificationNum)
                isSomethingValidatingNow = false
            }
        }
    }

    private suspend fun validateIdentificationNum(identificationNum: String): Boolean {
        val result = kennelInteractor.validateIdentificationNum(identificationNum)
        when (result) {
            ValidationResult.Success -> {
                _identificationNumberState.value = _identificationNumberState.value?.copy(
                    identificationNum = identificationNum,
                    validationState = ValidationState.Valid
                )
                return true
            }
            is ValidationResult.Failure -> {
                _identificationNumberState.value = _identificationNumberState.value?.copy(
                    identificationNum = identificationNum,
                    validationState = ValidationState.Invalid(message = result.errorMessage)
                )
                return false
            }
        }
    }

    fun onSaveBtnClick() {
        viewModelScope.launch {
            while (isSomethingValidatingNow) {
                delay(50)
            }
            var isAllValuesValid = true
            _saveBtnState.value = _saveBtnState.value?.copy(isEnabled = false)
            if (_cityFieldState.value?.validationState != ValidationState.Valid) {
                _cityFieldState.value = _cityFieldState.value?.copy(
                    validationState = ValidationState.Invalid(message = CITY_FIELD_ERROR)
                )
                isAllValuesValid = false
            }

            if (_emailFieldState.value?.validationState != ValidationState.Valid) {
                val email: String = _emailFieldState.value?.email ?: ""
                isAllValuesValid = validateEmail(email = email)
            }

            if (_houseNumberState.value?.validationState != ValidationState.Valid) {
                val houseNum: String = _houseNumberState.value?.houseNum ?: ""
                isAllValuesValid = validateHouseNum(house = houseNum)
            }

            if (_identificationNumberState.value?.validationState != ValidationState.Valid) {
                val identificationNum: String =
                    _identificationNumberState.value?.identificationNum ?: ""
                isAllValuesValid = validateIdentificationNum(identificationNum = identificationNum)
            }

            if (_kennelNameState.value?.validationState != ValidationState.Valid) {
                val kennelName: String = _kennelNameState.value?.kennelName ?: ""
                isAllValuesValid = validateKennelName(name = kennelName)
            }

            if (_phoneNumberState.value?.validationState != ValidationState.Valid) {
                val phoneNum: String = _phoneNumberState.value?.phoneNum ?: ""
                isAllValuesValid = validatePhoneNumber(phone = phoneNum)
            }

            if (_streetState.value?.validationState != ValidationState.Valid) {
                val streetName: String = _streetState.value?.streetName ?: ""
                isAllValuesValid = validateStreetName(street = streetName)
            }

            _saveBtnState.value = _saveBtnState.value?.copy(isEnabled = true)
            if (isAllValuesValid) {
                val kennelRequest: KennelRequest = makeKennelRequest()
                navigator.moveToKennelConfirmFragment(kennelRequest = kennelRequest)
            }
        }
    }

    private fun makeKennelRequest(): KennelRequest {
        val kennelRequest: KennelRequest = KennelRequest()
        when (_kennelAvatarState.value) {
            KennelAvatarState.EmptyValue -> kennelRequest.kennelAvtUri = ""
            is KennelAvatarState.Value -> kennelRequest.kennelAvtUri =
                (_kennelAvatarState.value as KennelAvatarState.Value).avatarUri
            null -> kennelRequest.kennelAvtUri = ""
        }
        kennelRequest.kennelName = _kennelNameState.value!!.kennelName
        kennelRequest.kennelPhoneNum = _phoneNumberState.value!!.phoneNum
        kennelRequest.kennelEmail = _emailFieldState.value!!.email
        kennelRequest.kennelCity =
            "${_cityFieldState.value!!.field.id},${_cityFieldState.value!!.field.fullName}"
        kennelRequest.kennelStreet = _streetState.value!!.streetName
        kennelRequest.kennelHouseNum = _houseNumberState.value!!.houseNum
        kennelRequest.kennelBuildingNum = _buildingState.value!!.building
        kennelRequest.kennelIdentifyNum =
            _identificationNumberState.value!!.identificationNum.toLong()

        return kennelRequest
    }


}