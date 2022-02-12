package mr.shtein.buddyandroidclient.screens.kennels

import android.content.Context
import android.hardware.input.InputManager
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.screens.profile.UserSettingsFragment


class KennelSettingsFragment : Fragment(R.layout.kennel_settings_fragment) {

    companion object {
        const val CITY_REQUEST_KEY = "new_city_request"
        const val CITY_BUNDLE_KEY = "new_city_bundle"
        const val AVATAR_FILE_NAME = "avatar"
    }

    private lateinit var avatarBtn: ShapeableImageView
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setListeners()
    }

    private fun initViews(view: View) {
        avatarBtn = view.findViewById(R.id.kennel_settings_avatar_button)
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
    }

    private fun setCity(cityInfo: String) {
        val (id, name, region) = cityInfo.split(",")
        val visibleCityInfo = "$name, $region"
        cityId = id.toLong()
        cityInput.setText(visibleCityInfo)
    }


}