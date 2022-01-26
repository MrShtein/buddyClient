package mr.shtein.buddyandroidclient.screens.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.utils.SharedPreferencesIO

class UserSettingsFragment: Fragment(R.layout.user_settings_fragment) {
    private var userName: TextInputEditText? = null
    private var userSurname: TextInputEditText? = null
    private var maleRadioBtn: RadioButton? = null
    private var femaleRadioBtn: RadioButton? = null
    private var city: TextInputEditText? = null
    private var phoneNumber: TextInputEditText? = null
    private var email: TextInputEditText? = null
    private var oldPwd: TextInputEditText? = null
    private var newPwd: TextInputEditText? = null
    private var repeatedNewPwd: TextInputEditText? = null
    private var saveBtn: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setUserCurrentUserSettings()
    }

    private fun initViews(view: View) {
        userName = view.findViewById(R.id.user_settings_name_input)
        userSurname = view.findViewById(R.id.user_settings_surname_input)
        maleRadioBtn = view.findViewById(R.id.user_settings_male_btn)
        femaleRadioBtn = view.findViewById(R.id.user_settings_female_btn)
        city = view.findViewById(R.id.user_settings_city_input)
        phoneNumber = view.findViewById(R.id.user_settings_phone_input)
        email = view.findViewById(R.id.user_settings_email_input)
        oldPwd = view.findViewById(R.id.user_settings_old_pwd_input)
        newPwd = view.findViewById(R.id.user_settings_new_pwd_input)
        repeatedNewPwd = view.findViewById(R.id.user_settings_repeat_new_pwd_input)
        saveBtn = view.findViewById(R.id.user_settings_save_btn)
    }

    private fun setUserCurrentUserSettings() {
        val storage = SharedPreferencesIO(requireContext(), SharedPreferencesIO.PERSISTENT_STORAGE_NAME)
        userName?.setText(storage.readString(SharedPreferencesIO.USER_NAME_KEY, ""))
        userSurname?.setText(storage.readString(SharedPreferencesIO.USER_SURNAME_KEY, ""))
        setGender(storage)
        city?.setText(storage.readString(SharedPreferencesIO.USER_CITY_KEY, ""))
        phoneNumber?.setText(storage.readString(SharedPreferencesIO.PHONE_NUMBER_KEY, ""))
        email?.setText(storage.readString(SharedPreferencesIO.USER_LOGIN_KEY, ""))
    }

    private fun setGender(sharedPrefIO: SharedPreferencesIO) {
        val gender = sharedPrefIO.readString(SharedPreferencesIO.USER_GENDER_KEY, "")
        when(gender) {
            "male" -> maleRadioBtn?.isChecked = true
            "female" -> femaleRadioBtn?.isChecked = true
        }
    }
}