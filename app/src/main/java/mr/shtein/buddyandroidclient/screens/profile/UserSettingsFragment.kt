package mr.shtein.buddyandroidclient.screens.profile

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.exceptions.validate.OldPasswordsIsNotValidException
import mr.shtein.buddyandroidclient.exceptions.validate.PasswordsIsDifferentException
import mr.shtein.buddyandroidclient.exceptions.validate.TooShortLengthException
import mr.shtein.buddyandroidclient.model.PersonRequest
import mr.shtein.buddyandroidclient.model.PersonResponse
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.utils.PasswordValidator
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class UserSettingsFragment: Fragment(R.layout.user_settings_fragment) {
    private var personId by Delegates.notNull<Long>()
    private lateinit var userName: TextInputEditText
    private lateinit var userSurname: TextInputEditText
    private lateinit var maleRadioBtn: RadioButton
    private lateinit var femaleRadioBtn: RadioButton
    private lateinit var city: TextInputEditText
    private lateinit var phoneNumber: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var oldPwd: TextInputEditText
    private lateinit var oldPwdContainer: TextInputLayout
    private lateinit var newPwd: TextInputEditText
    private lateinit var newPwdContainer: TextInputLayout
    private lateinit var repeatedNewPwd: TextInputEditText
    private lateinit var repeatedNewPwdContainer: TextInputLayout
    private lateinit var saveBtn: MaterialButton
    private lateinit var token: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setUserCurrentUserSettings()
        setListeners()
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
        oldPwdContainer = view.findViewById(R.id.user_settings_old_pwd_input_container)
        newPwd = view.findViewById(R.id.user_settings_new_pwd_input)
        newPwdContainer = view.findViewById(R.id.user_settings_new_pwd_input_container)
        repeatedNewPwd = view.findViewById(R.id.user_settings_repeat_new_pwd_input)
        repeatedNewPwdContainer = view.findViewById(R.id.user_settings_repeat_new_pwd_input_container)
        saveBtn = view.findViewById(R.id.user_settings_save_btn)

        val storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        token = storage.readString(SharedPreferences.TOKEN_KEY, "")
    }

    private fun setUserCurrentUserSettings() {
        val storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        userName.setText(storage.readString(SharedPreferences.USER_NAME_KEY, ""))
        userSurname.setText(storage.readString(SharedPreferences.USER_SURNAME_KEY, ""))
        setGender(storage)
        city.setText(storage.readString(SharedPreferences.USER_CITY_KEY, ""))
        phoneNumber.setText(storage.readString(SharedPreferences.PHONE_NUMBER_KEY, ""))
        email.setText(storage.readString(SharedPreferences.USER_LOGIN_KEY, ""))
        personId = storage.readLong(SharedPreferences.USER_ID_KEY, 0)
    }

    private fun setGender(sharedPref: SharedPreferences) {
        val gender = sharedPref.readString(SharedPreferences.USER_GENDER_KEY, "")
        when(gender) {
            "male" -> maleRadioBtn.isChecked = true
            "female" -> femaleRadioBtn.isChecked = true
        }
    }

    private fun setListeners() {
        saveBtn.setOnClickListener {
            saveBtn.isCheckable = false
            if (passwordsCheck()) {
                upgradePerson()
            }
        }

        oldPwd.setOnFocusChangeListener { _, _ ->
            oldPwdContainer.isErrorEnabled = false
        }

        newPwd.setOnFocusChangeListener {_, _ ->
            newPwdContainer.isErrorEnabled = false
        }

        repeatedNewPwd.setOnFocusChangeListener {_, _ ->
            repeatedNewPwdContainer.isErrorEnabled = false
        }
    }

    private fun passwordsCheck(): Boolean {
        val isPasswordChange: Boolean =
            oldPwd.text.toString() != ""
                    || newPwd.text.toString() != ""
                    || repeatedNewPwd.text.toString() != ""

        if (isPasswordChange) {
            val passwordValidator = PasswordValidator()
            try {
                passwordValidator.assertIsValidOldPassword(oldPwd.text.toString(), personId, token, oldPwdCallBack)
                passwordValidator.assertIsValidPassword(newPwd.text.toString())
                passwordValidator.assertIsValidRepeatPassword(repeatedNewPwd.text.toString(), newPwd)
                return true
            } catch (e: OldPasswordsIsNotValidException) {
                oldPwdContainer.error = e.message
                oldPwdContainer.isErrorEnabled = true
                saveBtn.isCheckable = true
            } catch (e: EmptyFieldException) {
                newPwdContainer.error = e.message
                newPwdContainer.isErrorEnabled = true
                saveBtn.isCheckable = true
            } catch (e: TooShortLengthException) {
                newPwdContainer.error = e.message
                newPwdContainer.isErrorEnabled = true
                saveBtn.isCheckable = true
            } catch (e: PasswordsIsDifferentException) {
                repeatedNewPwdContainer.error = e.message
                repeatedNewPwdContainer.isErrorEnabled = true
                saveBtn.isCheckable = true
            }

        }
        return false
    }

    private fun upgradePerson() {

        var gender = ""
        gender = if (maleRadioBtn.isChecked) "Мужской"
        else "Женский"
        val personRequest = PersonRequest(
            personId,
            userName.text.toString(),
            userSurname.text.toString(),
            gender,
            city.text.toString().toInt(),
            phoneNumber.text.toString(),
            email.text.toString(),
            newPwd.text.toString()
            )

        val retrofitService = Common.retrofitService
        val headerMap = hashMapOf<String, String>()
        headerMap["Authorization"] = "Bearer $token"
        retrofitService.upgradePersonInfo(headerMap, personRequest)
            .enqueue(object : Callback<PersonResponse> {
                override fun onResponse(
                    call: Call<PersonResponse>,
                    response: Response<PersonResponse>
                ) {
                    val personResponse: PersonResponse? = response.body()
                    if (personResponse?.isUpgrade == true) {
                        Toast.makeText(requireContext(), "It'ok", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), response.body()?.error?.message, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<PersonResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Скорее всего нет сети", Toast.LENGTH_LONG).show()
                }
            })
    }

    private val oldPwdCallBack = fun (error: String) {
        oldPwdContainer.isErrorEnabled = true
        oldPwdContainer.error = error
    }
}