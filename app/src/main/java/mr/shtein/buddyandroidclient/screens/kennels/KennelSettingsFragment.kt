package mr.shtein.buddyandroidclient.screens.kennels

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialSharedAxis
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.model.KennelRequest
import mr.shtein.buddyandroidclient.screens.profile.UserSettingsFragment
import mr.shtein.buddyandroidclient.setInsetsListenerForPadding
import mr.shtein.buddyandroidclient.utils.*
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.io.File
import java.lang.Exception


class KennelSettingsFragment : Fragment(R.layout.kennel_settings_fragment) {

    companion object {
        const val CITY_REQUEST_KEY = "new_city_request"
        const val CITY_BUNDLE_KEY = "new_city_bundle"
        private const val KENNEL_NAME_KEY = "isValidName"
        private const val PHONE_NUM_KEY = "isValidPhone"
        private const val EMAIL_KEY = "isValidEmail"
        private const val CITY_KEY = "isValidCity"
        private const val STREET_KEY = "isValidStreet"
        private const val HOUSE_KEY = "isValidHouseNum"
        private const val BUILDING_KEY = "building_key"
        private const val IDENTIFICATION_NUM_KEY = "isValidIdentificationNum"
        private const val AVATAR_URI_KEY = "avatar_uri"
        private const val SETTINGS_DATA_KEY = "settings_data"
        private const val KENNEL_AVATAR_FILE_NAME = "kennel_avt.jpeg"


    }

    private lateinit var avatarImg: AppCompatImageView
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
    private lateinit var storage: SharedPreferences

    private var isFromCityChoice: Boolean = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var cityId: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)

        setFragmentResultListener(CITY_REQUEST_KEY) { _, bundle ->
            val newCity = bundle.getString(UserSettingsFragment.CITY_BUNDLE_KEY)
            isFromCityChoice = bundle.getBoolean(UserSettingsFragment.IS_FROM_CITY_BUNDLE_KEY)
            if (newCity != null) {
                setCity(newCity)
                cityId = newCity.split(",")[0].toLong()
            }
        }

        getAvatarLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    val newPathForAvt = "${requireContext().filesDir}/${KENNEL_AVATAR_FILE_NAME}"
                    avatarImg.setImageURI(uri)
                    avatarUri = uri
                    copyFileToInternalStorage(uri)
                    storage.writeString(SharedPreferences.KENNEL_AVATAR_URI_KEY, newPathForAvt)
                }

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsetsListenerForPadding(view, left = false, top = true, right = false, bottom = false)
        initViews(view)
        initMaskForPhone(phoneNumberInput)
        setListeners()

    }

    private fun initViews(view: View) {
        storage = SharedPreferences(
            requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME
        )
        avatarImg = view.findViewById(R.id.kennel_settings_avatar_img)
        setAvatarImgIfExist()
        photoBtn = view.findViewById(R.id.kennel_settings_photo_btn)
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
        cityInput.setOnFocusChangeListener() { _, hasFocus ->
            if (hasFocus) {
                if (cityInputContainer.isErrorEnabled) cityInputContainer.isErrorEnabled = false
                cityInput.inputType = InputType.TYPE_NULL
                findNavController().navigate(R.id.action_kennelSettingsFragment_to_cityChoiceFragment)
            }
        }

        photoBtn.setOnClickListener {
            val dataType = "image/*"
            getAvatarLauncher.launch(dataType)
        }

        nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (nameContainer.isErrorEnabled) nameContainer.isErrorEnabled = false
            } else {
                val kennelName = nameInput.text.toString()
                validateAndHighlightError(
                    EmptyFieldValidator(),
                    KENNEL_NAME_KEY,
                    kennelName,
                    nameContainer
                )
            }
        }

        phoneNumberInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (phoneNumberInputContainer.isErrorEnabled)
                    phoneNumberInputContainer.isErrorEnabled = false
            } else {
                val kennelPhoneNum = phoneNumberInput.text.toString()
                validateAndHighlightError(
                    PhoneNumberValidator(),
                    PHONE_NUM_KEY,
                    kennelPhoneNum,
                    phoneNumberInputContainer
                )
            }
        }

        emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (emailInputContainer.isErrorEnabled) emailInputContainer.isErrorEnabled = false
            } else {
                val emailInputText = emailInput.text.toString()
                validateAndHighlightError(
                    SimpleEmailValidator(),
                    EMAIL_KEY,
                    emailInputText,
                    emailInputContainer
                )
            }
        }

        streetInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (streetInputContainer.isErrorEnabled) streetInputContainer.isErrorEnabled = false
            } else {
                val cityNameText = cityInput.text.toString()
                val streetText = streetInput.text.toString()
                validateAndHighlightError(
                    EmptyFieldValidator(),
                    CITY_KEY,
                    cityNameText,
                    cityInputContainer
                )
                validateAndHighlightError(
                    EmptyFieldValidator(),
                    STREET_KEY,
                    streetText,
                    streetInputContainer
                )
            }
        }

        houseNumberInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (houseNumberContainer.isErrorEnabled) houseNumberContainer.isErrorEnabled = false
            } else {
                val houseNum = houseNumberInput.text.toString()
                validateAndHighlightError(
                    EmptyFieldValidator(),
                    HOUSE_KEY,
                    houseNum,
                    houseNumberContainer
                )
            }
        }

        identificationNumberInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (identificationNumberInputContainer.isErrorEnabled)
                    identificationNumberInputContainer.isErrorEnabled = false
            } else {
                val identificationNum = identificationNumberInput.text.toString()
                validateAndHighlightError(
                    IdentificationNumberValidator(),
                    IDENTIFICATION_NUM_KEY,
                    identificationNum,
                    identificationNumberInputContainer
                )

            }
        }

        saveBtn.setOnClickListener {
            saveBtn.isClickable = false
            checkAllFieldsAndNavigate(it)
        }
    }

    private fun setCity(cityInfo: String) {
        val (id, name, region) = cityInfo.split(",")
        val visibleCityInfo = "$name, $region"
        cityId = id.toLong()
        cityInput.setText(visibleCityInfo)
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
            houseNumberContainer.error = errorText
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
            needScroll = false
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
                "${cityId},${cityInput.text.toString()}",
                streetInput.text.toString(),
                houseNumberInput.text.toString(),
                buildingNumberInput.text.toString(),
                identificationNumberInput.text.toString().toLong(),
                ""
            )
            storage.writeString(SharedPreferences.USER_ROLE_KEY, "")
            val kennelRequestJson = Gson().toJson(kennelRequest)
            val bundle = bundleOf(SETTINGS_DATA_KEY to kennelRequestJson)
            findNavController()
                .navigate(R.id.action_kennelSettingsFragment_to_kennelConfirmFragment, bundle)
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
            container.error = ex.message
            container.isErrorEnabled = true
        }
    }

    private fun setAvatarImgIfExist() {
        val avatarPath = storage.readString(SharedPreferences.KENNEL_AVATAR_URI_KEY, "")
        if (avatarPath != "") avatarImg.setImageURI(Uri.parse(avatarPath))
    }

    private suspend fun copyFileToInternalStorage(uri: Uri) = withContext(Dispatchers.IO) {
        val file = File(requireContext().filesDir, KENNEL_AVATAR_FILE_NAME)
        val fileStream = requireContext().contentResolver.openInputStream(uri)
        if (fileStream != null) {
            file.writeBytes(fileStream.readBytes())
        }
    }


}