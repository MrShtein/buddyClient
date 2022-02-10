package mr.shtein.buddyandroidclient.screens.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.exceptions.validate.OldPasswordsIsNotValidException
import mr.shtein.buddyandroidclient.exceptions.validate.PasswordsIsDifferentException
import mr.shtein.buddyandroidclient.exceptions.validate.TooShortLengthException
import mr.shtein.buddyandroidclient.model.PersonRequest
import mr.shtein.buddyandroidclient.model.PersonResponse
import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem
import mr.shtein.buddyandroidclient.model.response.EmailCheckRequest
import mr.shtein.buddyandroidclient.network.callback.MailCallback
import mr.shtein.buddyandroidclient.network.callback.PasswordCallBack
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.io.File
import java.io.InputStream
import kotlin.properties.Delegates


class UserSettingsFragment : Fragment(R.layout.user_settings_fragment) {

    companion object {
        const val NO_AUTHORIZE_TEXT = "Ошибка авторизации"
        const val MAIL_FAILURE_TEXT = "Нет интернета"
        const val IS_PERSON_WITH_EMAIL_EXIST = "Пользователь с таким email уже существует"
        const val DESTINATION_KEY = "destination_key"
        const val CITY_REQUEST_KEY = "new_city_request"
        const val CITY_BUNDLE_KEY = "new_city_bundle"
        const val IS_FROM_CITY_BUNDLE_KEY = "is_from_city_bundle"
        const val AVATAR_FILE_NAME = "avatar"
    }

    private var personId by Delegates.notNull<Long>()
    private var cityId by Delegates.notNull<Long>()
    private var isFromCityChoice: Boolean? = null


    private lateinit var userName: TextInputEditText
    private lateinit var userSurname: TextInputEditText
    private lateinit var maleRadioBtn: RadioButton
    private lateinit var femaleRadioBtn: RadioButton
    private lateinit var city: TextInputEditText
    private lateinit var cityContainer: TextInputLayout
    private lateinit var phoneNumber: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var emailContainer: TextInputLayout
    private lateinit var oldPwd: TextInputEditText
    private lateinit var oldPwdContainer: TextInputLayout
    private lateinit var newPwd: TextInputEditText
    private lateinit var newPwdContainer: TextInputLayout
    private lateinit var repeatedNewPwd: TextInputEditText
    private lateinit var repeatedNewPwdContainer: TextInputLayout
    private lateinit var saveBtn: MaterialButton
    private lateinit var token: String
    private lateinit var storage: SharedPreferences
    private lateinit var nestedScroll: NestedScrollView
    private lateinit var emailCallBack: MailCallback
    private lateinit var dialog: AlertDialog
    private lateinit var avatarImg: ShapeableImageView
    private lateinit var resultLauncher: ActivityResultLauncher<String>
    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    private var motionLayout: MotionLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(CITY_REQUEST_KEY) { requestKey, bundle ->
            val newCity = bundle.getString(CITY_BUNDLE_KEY)
            isFromCityChoice = bundle.getBoolean(IS_FROM_CITY_BUNDLE_KEY)
            if (newCity != null) setCity(newCity)
        }

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    avatarImg.setImageURI(uri)
                    coroutineScope.launch {
                        copyFileToInternalStorage(uri)
                    }
                    storage.writeString(SharedPreferences.USER_AVATAR_URI_KEY,
                        "${requireContext().filesDir}/$AVATAR_FILE_NAME")
                }
            }


        //val inflater = LayoutInflater.from(requireContext())
        // val userSettingsDialog = inflater.inflate(R.layout.user_settings_dialog, null)
        //
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setUserCurrentUserSettings()
        setListeners()
        initMaskForPhone(phoneNumber)

    }

    private fun initViews(view: View) {
        userName = view.findViewById(R.id.user_settings_name_input)
        userSurname = view.findViewById(R.id.user_settings_surname_input)
        maleRadioBtn = view.findViewById(R.id.user_settings_male_btn)
        femaleRadioBtn = view.findViewById(R.id.user_settings_female_btn)
        city = view.findViewById(R.id.user_settings_city_input)
        cityContainer = view.findViewById(R.id.user_settings_city_input_container)
        phoneNumber = view.findViewById(R.id.user_settings_phone_input)
        email = view.findViewById(R.id.user_settings_email_input)
        emailContainer = view.findViewById(R.id.user_settings_email_input_container)
        oldPwd = view.findViewById(R.id.user_settings_old_pwd_input)
        oldPwdContainer = view.findViewById(R.id.user_settings_old_pwd_input_container)
        newPwd = view.findViewById(R.id.user_settings_new_pwd_input)
        newPwdContainer = view.findViewById(R.id.user_settings_new_pwd_input_container)
        repeatedNewPwd = view.findViewById(R.id.user_settings_repeat_new_pwd_input)
        repeatedNewPwdContainer =
            view.findViewById(R.id.user_settings_repeat_new_pwd_input_container)
        saveBtn = view.findViewById(R.id.user_settings_save_btn)
        avatarImg = view.findViewById(R.id.user_settings_avatar_button)
        nestedScroll = view.findViewById(R.id.user_settings_scroll_view)

        storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        personId = storage.readLong(SharedPreferences.USER_ID_KEY, 0)


        emailCallBack = object : MailCallback {
            override fun onSuccess() {
                passwordsCheck()
            }

            override fun onFail(error: Exception) {
                dialog.dismiss()
                nestedScroll.smoothScrollTo(0, emailContainer.top)
                emailContainer.error = IS_PERSON_WITH_EMAIL_EXIST
                emailContainer.isErrorEnabled = true
            }

            override fun onFailure() {
                dialog.dismiss()
                Toast.makeText(requireContext(), MAIL_FAILURE_TEXT, Toast.LENGTH_LONG).show()
            }

            override fun onNoAuthorize() {
                dialog.dismiss()
                Toast.makeText(requireContext(), NO_AUTHORIZE_TEXT, Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun setUserCurrentUserSettings() {
        val storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        userName.setText(storage.readString(SharedPreferences.USER_NAME_KEY, ""))
        userSurname.setText(storage.readString(SharedPreferences.USER_SURNAME_KEY, ""))
        setGender(storage)
        setCity(storage)
        phoneNumber.setText(storage.readString(SharedPreferences.USER_PHONE_NUMBER_KEY, ""))
        email.setText(storage.readString(SharedPreferences.USER_LOGIN_KEY, ""))
        personId = storage.readLong(SharedPreferences.USER_ID_KEY, 0)

        setImageToAvatar()
    }

    private fun setGender(sharedPref: SharedPreferences) {
        val gender = sharedPref.readString(SharedPreferences.USER_GENDER_KEY, "")
        when (gender) {
            "Мужской" -> maleRadioBtn.isChecked = true
            "Женский" -> femaleRadioBtn.isChecked = true
        }
    }

    private fun setCity(sharedPref: SharedPreferences) {
        val cityInfo = sharedPref.readString(SharedPreferences.USER_CITY_KEY, "")
        val (id, name, region) = cityInfo.split(",")
        val visibleCityInfo = "$name, $region"
        cityId = id.toLong()
        city.setText(visibleCityInfo)
    }

    private fun setCity(cityInfo: String) {
        val (id, name, region) = cityInfo.split(",")
        val visibleCityInfo = "$name, $region"
        cityId = id.toLong()
        city.setText(visibleCityInfo)
    }

    private fun upgradePersonInfo() {
        emailCheck()
    }

    private fun setListeners() {

        cityContainer.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                cityContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (isFromCityChoice != null && isFromCityChoice == true) nestedScroll.scrollTo(
                    0,
                    cityContainer.top
                )
            }
        })


        saveBtn.setOnClickListener {
            saveBtn.isCheckable = false
            createAndShowDialog()
            saveBtn.isCheckable = true
        }

        avatarImg.setOnClickListener {
            val type = "image/*"
            resultLauncher.launch(type)
        }

        oldPwd.setOnFocusChangeListener { _, _ ->
            oldPwdContainer.isErrorEnabled = false
        }

        newPwd.setOnFocusChangeListener { _, _ ->
            newPwdContainer.isErrorEnabled = false
        }

        repeatedNewPwd.setOnFocusChangeListener { _, _ ->
            repeatedNewPwdContainer.isErrorEnabled = false
        }

        email.setOnFocusChangeListener { _, _ ->
            emailContainer.isErrorEnabled = false
        }

        email.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == 67) emailContainer.isErrorEnabled = false
            false
        }

        city.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                findNavController().navigate(R.id.action_userSettingsFragment_to_cityChoiceFragment)
            }

        }
    }

    private fun emailCheck() {
        val emailForCheck = email.text.toString()
        val emailCheckRequest = EmailCheckRequest(emailForCheck, personId)
        val emailValidator = EmailValidator(emailCheckRequest, emailCallBack, dialog)
        emailValidator.emailChecker(email, emailContainer) //TODO Изменить логику валидации
    }

    private fun passwordsCheck() {
        val isPasswordChange: Boolean =
            oldPwd.text.toString() != ""
                    || newPwd.text.toString() != ""
                    || repeatedNewPwd.text.toString() != ""

        if (isPasswordChange) {
            val passwordValidator = PasswordValidator()
            try {
                passwordValidator.assertIsValidPassword(newPwd.text.toString())
                passwordValidator.assertIsValidRepeatPassword(
                    repeatedNewPwd.text.toString(),
                    newPwd
                )
                token = storage.readString(SharedPreferences.TOKEN_KEY, "")
                passwordValidator.assertIsValidOldPassword(
                    oldPwd.text.toString(),
                    personId,
                    token,
                    oldPwdCallBack
                )

            } catch (e: OldPasswordsIsNotValidException) {
                dialog.dismiss()
                oldPwdContainer.error = e.message
                oldPwdContainer.isErrorEnabled = true
                saveBtn.isCheckable = true
            } catch (e: EmptyFieldException) {
                dialog.dismiss()
                newPwdContainer.error = e.message
                newPwdContainer.isErrorEnabled = true
                saveBtn.isCheckable = true
            } catch (e: TooShortLengthException) {
                dialog.dismiss()
                newPwdContainer.error = e.message
                newPwdContainer.isErrorEnabled = true
                saveBtn.isCheckable = true
            } catch (e: PasswordsIsDifferentException) {
                dialog.dismiss()
                repeatedNewPwdContainer.error = e.message
                repeatedNewPwdContainer.isErrorEnabled = true
                saveBtn.isCheckable = true
            }

        } else {
            upgradePerson()
        }
    }

    private fun upgradePerson() {


        var gender = getGender()

        val personRequest = PersonRequest(
            personId,
            userName.text.toString(),
            userSurname.text.toString(),
            gender,
            cityId,
            phoneNumber.text.toString(),
            email.text.toString(),
            newPwd.text.toString()
        )

        val retrofitService = Common.retrofitService
        val headerMap = hashMapOf<String, String>()
        token = storage.readString(SharedPreferences.TOKEN_KEY, "")
        headerMap["Authorization"] = "Bearer $token"
        retrofitService.upgradePersonInfo(headerMap, personRequest)
            .enqueue(object : Callback<PersonResponse> {
                override fun onResponse(
                    call: Call<PersonResponse>,
                    response: Response<PersonResponse>
                ) {
                    val personResponse: PersonResponse? = response.body()
                    if (personResponse?.isUpgrade == true) {
                        saveNewDataToStore(personResponse.newToken)
                        motionLayout?.transitionToEnd {
                            Toast.makeText(requireContext(), "TEST_TEST", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            response.body()?.error?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PersonResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Скорее всего нет сети", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    private fun saveNewDataToStore(token: String) {
        storage.writeString(SharedPreferences.USER_LOGIN_KEY, email.text.toString())
        storage.writeString(SharedPreferences.USER_NAME_KEY, userName.text.toString())
        storage.writeString(SharedPreferences.USER_SURNAME_KEY, userSurname.text.toString())
        storage.writeString(SharedPreferences.USER_PHONE_NUMBER_KEY, phoneNumber.text.toString())
        val genderForSave = getGender()
        storage.writeString(SharedPreferences.USER_GENDER_KEY, genderForSave)
        if (token != "") {
            storage.writeString(SharedPreferences.TOKEN_KEY, token)

            Log.d("token", storage.readString(SharedPreferences.TOKEN_KEY, ""))
            Log.d("token", token)
        }
    }

    private fun getGender(): String {
        return if (maleRadioBtn.isChecked) "Мужской"
        else "Женский"
    }

    private val oldPwdCallBack = object : PasswordCallBack {
        override fun onSuccess() {
            upgradePerson()
        }

        override fun onFail(error: String) {
            oldPwdContainer.isErrorEnabled = true
            oldPwdContainer.error = error
        }

        override fun onFailure() {
            Toast.makeText(requireContext(), "Ошибка в сети", Toast.LENGTH_LONG).show()
        }

        override fun onNoAuthorize() {
            Toast.makeText(requireContext(), "Нет авторизации", Toast.LENGTH_LONG).show()
        }
    }

    private fun initMaskForPhone(phoneNumberInput: TextInputEditText) {
        val maskImpl: MaskImpl = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        maskImpl.isHideHardcodedHead = true
        val watcher = MaskFormatWatcher(maskImpl)
        watcher.installOn(phoneNumberInput)
    }

    private fun makeStringForCityStorage(city: CityChoiceItem): String {
        return "${city.city_id},${city.name},${city.region}"
    }

    private fun createAndShowDialog() {

        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)

            .setView(R.layout.user_settings_dialog)
            .setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
            .show()


        val positiveDialogBtn: Button? = dialog.findViewById(R.id.user_settings_dialog_positive_btn)
        val negativeDialogBtn: Button? = dialog.findViewById(R.id.user_settings_dialog_negative_btn)
        val okBtn: Button? = dialog.findViewById(R.id.user_settings_dialog_ok_btn)

        motionLayout = dialog.findViewById(R.id.user_settings_dialog_motion_layout)

        negativeDialogBtn?.setOnClickListener {
            dialog.cancel()
        }

        positiveDialogBtn?.setOnClickListener {
            upgradePersonInfo()
        }

        okBtn?.setOnClickListener {
            dialog.dismiss()
            findNavController().popBackStack()
        }
    }

    private fun setImageToAvatar() {
        val imageUri = storage.readString(SharedPreferences.USER_AVATAR_URI_KEY, "")
        if (imageUri == "") {
            avatarImg.setImageResource(R.drawable.user_profile_photo_hint)
        } else {
            avatarImg.setImageURI(Uri.parse(imageUri))
        }
    }

    private suspend fun copyFileToInternalStorage(uri: Uri) = withContext(Dispatchers.IO) {
        val file = File(requireContext().filesDir, AVATAR_FILE_NAME)
        val fileStream = requireContext().contentResolver.openInputStream(uri)
        if (fileStream != null) {
            file.writeBytes(fileStream.readBytes())
        }
    }


}