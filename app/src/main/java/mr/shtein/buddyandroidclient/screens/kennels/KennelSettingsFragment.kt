package mr.shtein.buddyandroidclient.screens.kennels

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mr.shtein.buddyandroidclient.R


class KennelSettingsFragment : Fragment(R.layout.kennel_settings_fragment) {

    private lateinit var avatarBtn: ShapeableImageView
    private lateinit var nameContainer: TextInputLayout
    private lateinit var nameInput: TextInputEditText
    private lateinit var phoneNumberInputContainer: TextInputLayout
    private lateinit var phoneNumberInput: TextInputEditText
    private lateinit var emailInputContainer: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var cityInput: TextInputEditText
    private lateinit var streetInputContainer: TextInputLayout
    private lateinit var streetInput: TextInputEditText
    private lateinit var houseNumberContainer: TextInputLayout
    private lateinit var houseNumberInput: TextInputEditText
    private lateinit var buildingNumberInput: TextInputEditText
    private lateinit var identificationNumberInputContainer: TextInputLayout
    private lateinit var identificationNumberInput: TextInputEditText
    private lateinit var saveBtn: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    private fun initViews(view: View) {
        avatarBtn = view.findViewById(R.id.kennel_settings_avatar_button)
        nameContainer = view.findViewById(R.id.kennel_settings_organization_name_input_container)
        nameInput = view.findViewById(R.id.kennel_settings_organization_name_input)
        phoneNumberInputContainer = view.findViewById(R.id.kennel_settings_phone_number_input_container)
    }
}