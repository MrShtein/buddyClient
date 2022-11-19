package mr.shtein.buddyandroidclient.screens.profile

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
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
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.data.repository.UserRepository
import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.exceptions.validate.OldPasswordsIsNotValidException
import mr.shtein.buddyandroidclient.exceptions.validate.PasswordsIsDifferentException
import mr.shtein.buddyandroidclient.exceptions.validate.TooShortLengthException
import mr.shtein.model.PersonRequest
import mr.shtein.model.PersonResponse
import mr.shtein.model.CityChoiceItem
import mr.shtein.model.EmailCheckRequest
import mr.shtein.buddyandroidclient.network.callback.MailCallback
import mr.shtein.buddyandroidclient.network.callback.PasswordCallBack
import mr.shtein.network.NetworkService
import mr.shtein.buddyandroidclient.setInsetsListenerForPadding
import mr.shtein.buddyandroidclient.utils.*
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.io.File
import kotlin.properties.Delegates


class UserSettingsFragment : Fragment(R.layout.user_settings_fragment) {

    companion object {
        const val NO_AUTHORIZE_TEXT = "Ошибка авторизации"
        const val MAIL_FAILURE_TEXT = "Что-то не так с сетью, попробуйте позже"
        const val IS_PERSON_WITH_EMAIL_EXIST = "Пользователь с таким email уже существует"
        const val CITY_REQUEST_KEY = "new_city_request"
        const val CITY_BUNDLE_KEY = "new_city_bundle"
        const val IS_FROM_CITY_BUNDLE_KEY = "is_from_city_bundle"
        const val PERSON_AVATAR_FILE_NAME = "person_avatar"
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
    private lateinit var nestedScroll: NestedScrollView
    private lateinit var emailCallBack: MailCallback
    private lateinit var dialog: AlertDialog
    private lateinit var avatarImg: ShapeableImageView
    private lateinit var resultLauncher: ActivityResultLauncher<String>
    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var isTextChange = false
    private val networkService: NetworkService by inject()
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val passwordValidator: PasswordEmptyFieldValidator by inject()
    private val networkUserRepository: UserRepository by inject()

    private var motionLayout: MotionLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

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
                    userPropertiesRepository
                        .saveUserUri(
                            "${requireContext().filesDir}/$PERSON_AVATAR_FILE_NAME"
                        )
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsetsListenerForPadding(view, left = false, top = true, right = false, bottom = false)
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

        personId = userPropertiesRepository.getUserId()


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
        userName.setText(userPropertiesRepository.getUserName())
        userSurname.setText(userPropertiesRepository.getUserSurname())
        setGender()
        setCity()
        phoneNumber.setText(userPropertiesRepository.getUserPhoneNumber())
        email.setText(userPropertiesRepository.getUserLogin())
        personId = userPropertiesRepository.getUserId()
        if (!isTextChange) {
            saveBtn.setBackgroundColor(requireContext().getColor(R.color.grey3))
        }

        setImageToAvatar()
    }

    private fun setGender() {
        val gender = userPropertiesRepository.getUserGender()
        when (gender) {
            "Мужской" -> maleRadioBtn.isChecked = true
            "Женский" -> femaleRadioBtn.isChecked = true
        }
    }

    private fun setCity() {
        val cityInfo = userPropertiesRepository.getUserCity()
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
        val textWatcher = getTextWatcher()
        userName.addTextChangedListener(textWatcher)
        userSurname.addTextChangedListener(textWatcher)

        maleRadioBtn.setOnClickListener {
            isTextChange = true
            changeSaveBtnColor(isTextChange)
        }

        femaleRadioBtn.setOnClickListener {
            isTextChange = true
            changeSaveBtnColor(isTextChange)
        }

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
        city.addTextChangedListener(textWatcher)
        city.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                findNavController().navigate(R.id.action_userSettingsFragment_to_cityChoiceFragment)
            }
        }

        phoneNumber.addTextChangedListener(textWatcher)

        email.addTextChangedListener(textWatcher)
        email.setOnFocusChangeListener { _, _ ->
            emailContainer.isErrorEnabled = false
        }
        email.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == 67) emailContainer.isErrorEnabled = false
            false
        }

        oldPwd.setOnFocusChangeListener { _, _ ->
            oldPwdContainer.isErrorEnabled = false
        }
        oldPwd.addTextChangedListener(textWatcher)

        newPwd.setOnFocusChangeListener { _, _ ->
            newPwdContainer.isErrorEnabled = false
        }
        newPwd.addTextChangedListener(textWatcher)

        repeatedNewPwd.setOnFocusChangeListener { _, _ ->
            repeatedNewPwdContainer.isErrorEnabled = false
        }
        repeatedNewPwd.addTextChangedListener(textWatcher)

        saveBtn.setOnClickListener {
            saveBtn.isCheckable = false
            createAndShowDialog()
            saveBtn.isCheckable = true
        }
        avatarImg.setOnClickListener {
            val type = "image/*"
            resultLauncher.launch(type)
        }
    }

    private fun emailCheck() {
        val emailForCheck = email.text.toString()
        val emailCheckRequest = EmailCheckRequest(emailForCheck, personId)
        val emailValidator = FullEmailValidator(emailCheckRequest, emailCallBack, dialog, coroutineScope, networkUserRepository)
        emailValidator.emailChecker(email, emailContainer) //TODO Изменить логику валидации
    }

    private fun passwordsCheck() {
        val isPasswordChange: Boolean =
            oldPwd.text.toString() != ""
                    || newPwd.text.toString() != ""
                    || repeatedNewPwd.text.toString() != ""

        if (isPasswordChange) {
            try {
                passwordValidator.assertIsValidPassword(newPwd.text.toString())
                passwordValidator.assertIsValidRepeatPassword(
                    repeatedNewPwd.text.toString(),
                    newPwd
                )
                token = userPropertiesRepository.getUserToken()
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


        val gender = getGender()

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

        val headerMap = hashMapOf<String, String>()
        token = userPropertiesRepository.getUserToken()
        headerMap["Authorization"] = token
        networkService.upgradePersonInfo(headerMap, personRequest)
            .enqueue(object : Callback<PersonResponse> {
                override fun onResponse(
                    call: Call<PersonResponse>,
                    response: Response<PersonResponse>
                ) {
                    val personResponse: PersonResponse? = response.body()
                    if (personResponse?.isUpgrade == true) {
                        saveNewDataToStore(personResponse.newToken)
                        motionLayout?.transitionToEnd()
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
        userPropertiesRepository.saveUserLogin(email.text.toString())
        userPropertiesRepository.saveUserName(userName.text.toString())
        userPropertiesRepository.saveUserSurname(userSurname.text.toString())
        userPropertiesRepository.saveUserPhoneNumber(phoneNumber.text.toString())
        val genderForSave = getGender()
        userPropertiesRepository.saveUserGender(genderForSave)
        if (token != "") {
            userPropertiesRepository.saveUserToken(token)
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
        val imageUri = userPropertiesRepository.getUserUri()
        if (imageUri !== "") {
            avatarImg.setImageURI(Uri.parse(imageUri))
            avatarImg.scaleType = ImageView.ScaleType.CENTER_CROP
            avatarImg.background = null
        }
    }

    private suspend fun copyFileToInternalStorage(uri: Uri) = withContext(Dispatchers.IO) {
        val file = File(requireContext().filesDir, PERSON_AVATAR_FILE_NAME)
        val fileStream = requireContext().contentResolver.openInputStream(uri)
        if (fileStream != null) {
            file.writeBytes(fileStream.readBytes())
        }
    }

    override fun onStop() {
        super.onStop()
        isTextChange = false
    }

    private fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (!isTextChange) {
                    isTextChange = true
                    changeSaveBtnColor(isTextChange)
                }
            }
        }
    }

    private fun changeSaveBtnColor(isTextChange: Boolean) {
        if (isTextChange) {
            saveBtn.setBackgroundColor(requireContext().getColor(R.color.cian5))
        } else {
            saveBtn.setBackgroundColor(requireContext().getColor(R.color.grey3))
        }
    }


}