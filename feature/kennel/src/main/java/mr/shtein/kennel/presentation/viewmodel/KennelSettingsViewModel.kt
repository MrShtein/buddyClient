package mr.shtein.kennel.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mr.shtein.kennel.CityField
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.kennel_settings.*

class KennelSettingsViewModel(
    val navigator: KennelNavigation
) : ViewModel() {

    companion object {
        private const val DEFAULT_INPUT_VALUE = ""
        private val EMPTY_CITY_VALUE = CityField()
    }

    private val _cityFieldState: MutableLiveData<CityFieldState> = MutableLiveData()
    val cityFieldState: LiveData<CityFieldState> = _cityFieldState

    private val _emailFieldState: MutableLiveData<EmailFieldState> = MutableLiveData()
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
        _cityFieldState.value = CityFieldState.Value(value = EMPTY_CITY_VALUE)
        _emailFieldState.value = EmailFieldState.Value(value = DEFAULT_INPUT_VALUE)
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
        _cityFieldState.value = CityFieldState.Value(
            value = CityField(id = cityId.toLong(), fullName = "$cityName, $regionName")
        )
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
            _cityFieldState.value = CityFieldState.Value(value = EMPTY_CITY_VALUE)
            navigator.moveToCityChoiceFromKennelSettings()
        }
    }
}