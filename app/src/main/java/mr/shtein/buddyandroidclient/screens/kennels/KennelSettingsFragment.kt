package mr.shtein.buddyandroidclient.screens.kennels

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.exceptions.validate.ValidationException
import mr.shtein.buddyandroidclient.screens.profile.UserSettingsFragment
import mr.shtein.buddyandroidclient.utils.KennelValidationStore
import mr.shtein.buddyandroidclient.utils.VariousValidator
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher


class KennelSettingsFragment : Fragment(R.layout.kennel_settings_fragment) {

    companion object {
        const val CITY_REQUEST_KEY = "new_city_request"
        const val CITY_BUNDLE_KEY = "new_city_bundle"
        const val AVATAR_FILE_NAME = "avatar"
    }

    private lateinit var avatarImg: ShapeableImageView
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
    private lateinit var getAvatarLauncher: ActivityResultLauncher<String>
    private var avatarUri: Uri? = null
    private var validationStoreForFields = KennelValidationStore()

    private var isFromCityChoice: Boolean = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var cityId: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(CITY_REQUEST_KEY) { _, bundle ->
            val newCity = bundle.getString(UserSettingsFragment.CITY_BUNDLE_KEY)
            isFromCityChoice = bundle.getBoolean(UserSettingsFragment.IS_FROM_CITY_BUNDLE_KEY)
            if (newCity != null) setCity(newCity)
        }

        getAvatarLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                avatarImg.setImageURI(uri)
                avatarUri = uri
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initMaskForPhone(phoneNumberInput)
        setListeners()

    }

    private fun initViews(view: View) {
        avatarImg = view.findViewById(R.id.kennel_settings_avatar_button)
        nameContainer = view.findViewById(R.id.kennel_settings_organization_name_input_container)
        nameInput = view.findViewById(R.id.kennel_settings_organization_name_input)
        phoneNumberInputContainer =
            view.findViewById(R.id.kennel_settings_phone_number_input_container)
        phoneNumberInput = view.findViewById(R.id.kennel_settings_phone_number_input)
        emailInputContainer = view.findViewById(R.id.kennel_settings_email_input_container)
        emailInput = view.findViewById(R.id.kennel_settings_email_input)
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

    }

    private fun setListeners() {
        cityInput.setOnFocusChangeListener() {_, isFocused ->
            if (isFocused) {
                cityInput.inputType = InputType.TYPE_NULL
                findNavController().navigate(R.id.action_kennelSettingsFragment_to_cityChoiceFragment2)
            }
        }

        avatarImg.setOnClickListener {
            val dataType = "image/*"
            getAvatarLauncher.launch(dataType)
        }

        nameInput.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                if (nameContainer.isErrorEnabled) nameContainer.isErrorEnabled = false
                nameInput.setText("")
                nameInput.setTextColor(requireContext().getColor(R.color.black))
            } else {
                val kennelName = nameInput.text.toString()
                try {
                    validationStoreForFields.isValidName = VariousValidator.isEmptyField(kennelName)
                } catch (ex: EmptyFieldException) {
                    nameContainer.error = ex.message
                    nameContainer.isErrorEnabled = true
                }
            }
        }

        phoneNumberInput.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                if (phoneNumberInputContainer.isErrorEnabled) {
                    phoneNumberInputContainer.isErrorEnabled = false
                } else {
                    phoneNumberInput.setTextColor(requireContext().getColor(R.color.black))
                    phoneNumberInput.setText("")
                }
                nameInput.setTextColor(requireContext().getColor(R.color.black))
            } else {
                val kennelPhoneNum = phoneNumberInput.text.toString()
                try {
                    validationStoreForFields.isValidPhone = VariousValidator.isValidPhoneNum(kennelPhoneNum)
                } catch (ex: ValidationException) {
                    phoneNumberInputContainer.error = ex.message
                    phoneNumberInputContainer.isErrorEnabled = true
                }
            }
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


}