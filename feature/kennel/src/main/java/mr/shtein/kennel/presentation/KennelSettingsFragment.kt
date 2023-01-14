package mr.shtein.kennel.presentation

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.shtein.data.model.KennelRequest
import mr.shtein.data.repository.KennelPropertiesRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.kennel_settings.*
import mr.shtein.kennel.presentation.viewmodel.KennelSettingsViewModel
import mr.shtein.kennel.util.KennelValidationStore
import mr.shtein.ui_util.setInsetsListenerForPadding
import mr.shtein.ui_util.setStatusBarColor
import mr.shtein.util.ImageCompressor
import mr.shtein.util.validator.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.io.File
import java.lang.Exception


class KennelSettingsFragment : Fragment(R.layout.kennel_settings_fragment) {

    companion object {
        const val CITY_REQUEST_KEY = "new_city_request"
        private const val KENNEL_NAME_KEY = "isValidName"
        private const val TAG = "KennelSettingsFragment"
        private const val PHONE_NUM_KEY = "isValidPhone"
        private const val EMAIL_KEY = "isValidEmail"
        private const val CITY_KEY = "isValidCity"
        private const val STREET_KEY = "isValidStreet"
        private const val HOUSE_KEY = "isValidHouseNum"
        private const val IDENTIFICATION_NUM_KEY = "isValidIdentificationNum"
        private const val KENNEL_AVATAR_FILE_NAME = "temp_kennel_avt.webp"
        const val CITY_BUNDLE_KEY = "new_city_bundle"
        const val IS_FROM_CITY_BUNDLE_KEY = "is_from_city_bundle"
    }

    private lateinit var avatarImg: AppCompatImageView
    private lateinit var avatarCancelBtn: AppCompatImageView
    private lateinit var nameContainer: TextInputLayout
    private lateinit var nameInput: TextInputEditText
    private lateinit var phoneNumberInputContainer: TextInputLayout
    private lateinit var phoneNumberInput: TextInputEditText
    private lateinit var emailInputContainer: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var cityInputContainer: TextInputLayout
    private lateinit var cityInput: TextInputEditText
    private lateinit var streetInputContainer: TextInputLayout
    private lateinit var streetInput: TextInputEditText
    private lateinit var houseNumberContainer: TextInputLayout
    private lateinit var houseNumberInput: TextInputEditText
    private lateinit var buildingNumberInput: TextInputEditText
    private lateinit var identificationNumberInputContainer: TextInputLayout
    private lateinit var identificationNumberInput: TextInputEditText
    private lateinit var saveBtn: MaterialButton
    private lateinit var photoBtn: AppCompatImageButton
    private lateinit var nestedScroll: NestedScrollView
    private lateinit var getAvatarLauncher: ActivityResultLauncher<String>
    private var avatarUri: Uri? = null
    private var validationStoreForFields = KennelValidationStore()
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val kennelPropertiesRepository: KennelPropertiesRepository by inject()
    private val navigator: KennelNavigation by inject()
    private val kennelSettingsViewModel: KennelSettingsViewModel by viewModel()
    private val imageCompressor: ImageCompressor by inject()

    private var isFromCityChoice: Boolean = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)

        setFragmentResultListener(CITY_REQUEST_KEY) { _, bundle ->
            val newCity = bundle.getString(CITY_BUNDLE_KEY)
            isFromCityChoice = bundle.getBoolean(IS_FROM_CITY_BUNDLE_KEY)
            if (newCity != null) {
                kennelSettingsViewModel.onCityChanged(city = newCity)
            }
        }

        getAvatarLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    val avatar: ByteArray = requireContext()
                        .contentResolver
                        .openInputStream(uri)
                        ?.readBytes() ?: byteArrayOf()
                    val compressedAvatar: ByteArray = imageCompressor.compressImage(avatar)
                    val pathToTempAvatarFile =
                        "${requireContext().filesDir}/$KENNEL_AVATAR_FILE_NAME"
                    kennelSettingsViewModel.onAvatarChanged(
                        avatarPath = pathToTempAvatarFile,
                        avatarUri = uri.toString()
                    )
                    copyFileToInternalStorage(compressedAvatar)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layout: ConstraintLayout = view.findViewById(R.id.kennel_settings_constraint_layout)
        setInsetsListenerForPadding(layout, left = false, top = true, right = false, bottom = true)
        setStatusBarColor(isBlack = true)
        initViews(view)
        initMaskForPhone(phoneNumberInput)
        setListeners()

        kennelSettingsViewModel.cityFieldState.observe(viewLifecycleOwner) { cityState ->
            when (cityState.validationState) {
                is ValidationState.Valid -> {
                    cityInput.setText(cityState.field.fullName)
                    if (cityInputContainer.isErrorEnabled) {
                        cityInputContainer.isErrorEnabled = false
                    }
                }
                is ValidationState.Invalid -> {
                    cityInputContainer.error =
                        (cityState.validationState as ValidationState.Invalid).message
                    cityInputContainer.isErrorEnabled = true
                    cityInput.setText(cityState.field.fullName)
                    scrollToElementIfNeed(cityInputContainer)
                }
                null -> {
                    cityInput.setText(cityState.field.fullName)
                }
            }
        }

        kennelSettingsViewModel.kennelAvatarState.observe(viewLifecycleOwner) { avatarState ->
            when (avatarState) {
                KennelAvatarState.EmptyValue -> {
                    avatarCancelBtn.visibility = View.INVISIBLE
                    photoBtn.visibility = View.VISIBLE
                    avatarImg.setImageDrawable(null)
                    coroutineScope.launch {
                        deleteFileFromInternalStorage()
                    }
                }
                is KennelAvatarState.Value -> {
                    avatarCancelBtn.visibility = View.VISIBLE
                    photoBtn.visibility = View.INVISIBLE
                    avatarImg.setImageURI(avatarState.avatarUri.toUri())
                }
            }
        }

        kennelSettingsViewModel.emailFieldState.observe(viewLifecycleOwner) { emailState ->
            when (emailState.validationState) {
                is ValidationState.Valid -> {
                    emailInput.setText(emailState.email)
                    emailInput.setSelection(emailInput.length())
                }
                is ValidationState.Invalid -> {
                    emailInputContainer.isErrorEnabled = true
                    emailInputContainer.error =
                        (emailState.validationState as ValidationState.Invalid).message
                    emailInput.setText(emailState.email)
                    emailInput.setSelection(emailInput.length())
                    scrollToElementIfNeed(emailInputContainer)
                }
                null -> {
                    emailInput.setText(emailState.email)
                    emailInput.setSelection(emailInput.length())
                    if (emailInputContainer.isErrorEnabled) {
                        emailInputContainer.isErrorEnabled = false
                    }
                }
            }
        }

        kennelSettingsViewModel.kennelNameState.observe(viewLifecycleOwner) { nameState ->
            when (nameState.validationState) {
                is ValidationState.Valid -> {
                    nameInput.setText(nameState.kennelName)
                    nameInput.setSelection(nameInput.length())
                }
                is ValidationState.Invalid -> {
                    nameContainer.isErrorEnabled = true
                    nameContainer.error = nameState.validationState.message
                    nameInput.setText(nameState.kennelName)
                    nameInput.setSelection(nameInput.length())
                    scrollToElementIfNeed(nameContainer)
                }
                null -> {
                    nameInput.setText(nameState.kennelName)
                    nameInput.setSelection(nameInput.length())
                    if (nameContainer.isErrorEnabled) {
                        nameContainer.isErrorEnabled = false
                    }
                }
            }
        }

        kennelSettingsViewModel.phoneNumberState.observe(viewLifecycleOwner) { phoneState ->
            when (phoneState.validationState) {
                is ValidationState.Valid -> {
                    phoneNumberInput.setText(phoneState.phoneNum)
                    phoneNumberInput.setSelection(phoneNumberInput.length())
                }
                is ValidationState.Invalid -> {
                    phoneNumberInputContainer.isErrorEnabled = true
                    phoneNumberInputContainer.error = phoneState.validationState.message
                    phoneNumberInput.setText(phoneState.phoneNum)
                    phoneNumberInput.setSelection(phoneNumberInput.length())
                    scrollToElementIfNeed(phoneNumberInputContainer)
                }
                null -> {
                    phoneNumberInput.setText(phoneState.phoneNum)
                    phoneNumberInput.setSelection(phoneNumberInput.length())
                    if (phoneNumberInputContainer.isErrorEnabled) {
                        phoneNumberInputContainer.isErrorEnabled = false
                    }
                }
            }
        }

        kennelSettingsViewModel.streetState.observe(viewLifecycleOwner) { streetState ->
            when (streetState) {
                is StreetState.Value -> {
                    if (streetInputContainer.isErrorEnabled) {
                        streetInputContainer.isErrorEnabled = false
                    }
                }
                is StreetState.Error -> {
                    streetInputContainer.isErrorEnabled = true
                    streetInputContainer.error = streetState.message
                    scrollToElementIfNeed(streetInputContainer)
                }
            }
        }

        kennelSettingsViewModel.houseNumberState.observe(viewLifecycleOwner) { houseNumState ->
            when (houseNumState.validationState) {
                is ValidationState.Valid -> {
                    houseNumberInput.setText(houseNumState.houseNum)
                    houseNumberInput.setSelection(houseNumberInput.length())
                }
                is ValidationState.Invalid -> {
                    houseNumberContainer.isErrorEnabled = true
                    houseNumberContainer.error = houseNumState.validationState.message
                    houseNumberInput.setText(houseNumState.houseNum)
                    houseNumberInput.setSelection(houseNumberInput.length())
                    scrollToElementIfNeed(houseNumberContainer)
                }
                null -> {
                    houseNumberInput.setText(houseNumState.houseNum)
                    houseNumberInput.setSelection(houseNumberInput.length())
                    if (houseNumberContainer.isErrorEnabled) {
                        houseNumberContainer.isErrorEnabled = false
                    }
                }
            }
        }

        kennelSettingsViewModel.identificationNumberState.observe(viewLifecycleOwner) { identificationNumberState ->
            when (identificationNumberState.validationState) {
                is ValidationState.Valid -> {
                    identificationNumberInput.setText(identificationNumberState.identificationNum)
                    identificationNumberInput.setSelection(identificationNumberInput.length())
                }
                is ValidationState.Invalid -> {
                    identificationNumberInputContainer.isErrorEnabled = true
                    identificationNumberInputContainer.error =
                        identificationNumberState.validationState.message
                    identificationNumberInput.setText(identificationNumberState.identificationNum)
                    identificationNumberInput.setSelection(identificationNumberInput.length())
                    scrollToElementIfNeed(identificationNumberInputContainer)
                }
                null -> {
                    identificationNumberInput.setText(identificationNumberState.identificationNum)
                    identificationNumberInput.setSelection(identificationNumberInput.length())
                    if (identificationNumberInputContainer.isErrorEnabled) {
                        identificationNumberInputContainer.isErrorEnabled = false
                    }
                }
            }
        }

//        kennelSettingsViewModel.saveBtnState.observe(viewLifecycleOwner) { saveBtnState ->
//            saveBtn.isEnabled = !saveBtnState.isBlocked
//        }
    }

    private fun initViews(view: View) {
        avatarImg = view.findViewById(R.id.kennel_settings_avatar_img)
        photoBtn = view.findViewById(R.id.kennel_settings_photo_btn)
        avatarCancelBtn = view.findViewById(R.id.kennel_settings_cancel_avatar_btn)

        nameContainer = view.findViewById(R.id.kennel_settings_organization_name_input_container)
        nameInput = view.findViewById(R.id.kennel_settings_organization_name_input)
        phoneNumberInputContainer =
            view.findViewById(R.id.kennel_settings_phone_number_input_container)
        phoneNumberInput = view.findViewById(R.id.kennel_settings_phone_number_input)
        emailInputContainer = view.findViewById(R.id.kennel_settings_email_input_container)
        emailInput = view.findViewById(R.id.kennel_settings_email_input)
        cityInputContainer = view.findViewById(R.id.kennel_settings_city_input_container)
        cityInput = view.findViewById(R.id.kennel_settings_city_input)
        streetInputContainer = view.findViewById(R.id.kennel_settings_street_input_container)
        streetInput = view.findViewById(R.id.kennel_settings_street_input)
        houseNumberContainer = view.findViewById(R.id.kennel_settings_house_input_container)
        houseNumberInput = view.findViewById(R.id.kennel_settings_house_input)
        buildingNumberInput = view.findViewById(R.id.kennel_settings_building_input)
        identificationNumberInputContainer =
            view.findViewById(R.id.kennel_settings_identification_number_input_container)
        identificationNumberInput =
            view.findViewById(R.id.kennel_settings_identification_number_input)
        saveBtn = view.findViewById(R.id.kennel_settings_save_btn)
        nestedScroll = view.findViewById(R.id.kennel_settings_scroll_view)

    }

    private fun setListeners() {
        cityInput.setOnFocusChangeListener { _, hasFocus ->
            kennelSettingsViewModel.onCityInputFocusChanged(hasFocus = hasFocus)
        }

        photoBtn.setOnClickListener {
            val dataType = "image/*"
            getAvatarLauncher.launch(dataType)
        }

        avatarCancelBtn.setOnClickListener {
            kennelSettingsViewModel.onAvatarCancelBtnClicked()
        }

        nameInput.setOnFocusChangeListener { _, hasFocus ->
            kennelSettingsViewModel.onKennelNameFocusChanged(
                hasFocus = hasFocus,
                name = nameInput.text.toString()
            )
        }

        phoneNumberInput.setOnFocusChangeListener { _, hasFocus ->
            kennelSettingsViewModel.onPhoneNumberFocusChanged(
                hasFocus = hasFocus,
                phone = phoneNumberInput.text.toString()
            )
        }

        emailInput.setOnFocusChangeListener { _, hasFocus ->
            val emailInputText = emailInput.text.toString()
            kennelSettingsViewModel.onEmailInputFocusChanged(
                hasFocus = hasFocus,
                email = emailInputText
            )
        }

        streetInput.setOnFocusChangeListener { _, hasFocus ->
            kennelSettingsViewModel.onStreetFocusChanged(
                hasFocus = hasFocus,
                street = streetInput.text.toString()
            )
        }

        houseNumberInput.setOnFocusChangeListener { _, hasFocus ->
            kennelSettingsViewModel.onHouseFocusChanged(
                hasFocus = hasFocus,
                house = houseNumberInput.text.toString()
            )
        }

        identificationNumberInput.setOnFocusChangeListener { _, hasFocus ->
            kennelSettingsViewModel.onIdentificationNumFocusChanged(
                hasFocus = hasFocus,
                identificationNum = identificationNumberInput.text.toString()
            )
        }

        saveBtn.setOnClickListener {
            saveBtn.isClickable = false
            checkAllFieldsAndNavigate(it)
        }
    }

    private fun initMaskForPhone(phoneNumberInput: TextInputEditText) {
        val maskImpl: MaskImpl = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        maskImpl.isHideHardcodedHead = true
        val watcher = MaskFormatWatcher(maskImpl)
        watcher.installOn(phoneNumberInput)
    }

    private fun checkAllFieldsAndNavigate(btn: View) {
        val errorText = "Вы не заполнили данное поле"
        var needScroll = true
        var goToNextScreen = true
        if (validationStoreForFields.mapOfValues[KENNEL_NAME_KEY] == false) {
            scrollToElementIfNeed(nameContainer)
            needScroll = false
            goToNextScreen = false
            nameContainer.isErrorEnabled = true
            nameContainer.error = errorText
            btn.isClickable = true
        }
        if (validationStoreForFields.mapOfValues[PHONE_NUM_KEY] == false) {
            if (needScroll) scrollToElementIfNeed(phoneNumberInputContainer)
            needScroll = false
            goToNextScreen = false
            phoneNumberInputContainer.isErrorEnabled = true
            phoneNumberInputContainer.error = errorText
            btn.isClickable = true
        }
        if (validationStoreForFields.mapOfValues[EMAIL_KEY] == false) {
            if (needScroll) scrollToElementIfNeed(emailInputContainer)
            needScroll = false
            goToNextScreen = false
            emailInputContainer.isErrorEnabled = true
            emailInputContainer.error = errorText
            btn.isClickable = true
        }
        if (validationStoreForFields.mapOfValues[CITY_KEY] == false) {
            if (needScroll) scrollToElementIfNeed(cityInputContainer)
            needScroll = false
            goToNextScreen = false
            cityInputContainer.isErrorEnabled = true
            cityInputContainer.error = errorText
            btn.isClickable = true
        }
        if (validationStoreForFields.mapOfValues[STREET_KEY] == false) {
            if (needScroll) scrollToElementIfNeed(streetInputContainer)
            needScroll = false
            goToNextScreen = false
            streetInputContainer.isErrorEnabled = true
            streetInputContainer.error = errorText
            btn.isClickable = true
        }
        if (validationStoreForFields.mapOfValues[HOUSE_KEY] == false) {
            if (needScroll) scrollToElementIfNeed(houseNumberContainer)
            needScroll = false
            goToNextScreen = false
            houseNumberContainer.isErrorEnabled = true
            btn.isClickable = true
        }
        validateAndHighlightError(
            IdentificationNumberValidator(),
            IDENTIFICATION_NUM_KEY,
            identificationNumberInput.text.toString(),
            identificationNumberInputContainer
        )
        if (validationStoreForFields.mapOfValues[IDENTIFICATION_NUM_KEY] == false) {
            if (needScroll) scrollToElementIfNeed(houseNumberContainer)
            goToNextScreen = false
            btn.isClickable = true
        }

        if (goToNextScreen) {
            val avatarStrUri = if (avatarUri == null) "" else avatarUri.toString()
            val kennelRequest = KennelRequest(
                1,
                avatarStrUri,
                nameInput.text.toString(),
                phoneNumberInput.text.toString(),
                emailInput.text.toString(),
                cityInput.text.toString(),
                streetInput.text.toString(),
                houseNumberInput.text.toString(),
                buildingNumberInput.text.toString(),
                identificationNumberInput.text.toString().toLong(),
                ""
            )
            userPropertiesRepository.saveUserRole("")
            navigator.moveToKennelConfirmFragment(kennelRequest = kennelRequest)
        }
    }

    private fun scrollToElementIfNeed(element: View) {
        if (element.top < nestedScroll.scrollY) nestedScroll.scrollTo(0, element.top)
    }

    private fun validateAndHighlightError(
        validator: Validator,
        valueNameInStore: String,
        valueForValidating: String,
        container: TextInputLayout
    ) {
        try {
            val isValidValue = validator.validateValue(valueForValidating)
            validationStoreForFields.mapOfValues[valueNameInStore] = isValidValue
        } catch (ex: Exception) {
            if (container.id != R.id.kennel_settings_house_input_container) {
                container.error = ex.message
            } else {
                container.error = "1"
            }
            container.isErrorEnabled = true
        }
    }

    private suspend fun copyFileToInternalStorage(avatarInBytes: ByteArray) =
        withContext(Dispatchers.IO) {
            val file = File(requireContext().filesDir, KENNEL_AVATAR_FILE_NAME)
            file.writeBytes(avatarInBytes)
        }

    private suspend fun deleteFileFromInternalStorage() {
        withContext(Dispatchers.IO) {
            val file = File(requireContext().filesDir, KENNEL_AVATAR_FILE_NAME)
            file.delete()
        }
    }

}