package mr.shtein.buddyandroidclient

import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.shtein.buddyandroidclient.exceptions.validate.*
import mr.shtein.buddyandroidclient.model.Person
import mr.shtein.buddyandroidclient.model.response.EmailCheckRequest
import mr.shtein.buddyandroidclient.network.callback.MailCallback
import mr.shtein.buddyandroidclient.data.repository.RetrofitUserRepository
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.data.repository.UserRepository
import mr.shtein.buddyandroidclient.utils.FullEmailValidator
import mr.shtein.buddyandroidclient.utils.NameValidator
import mr.shtein.buddyandroidclient.utils.PasswordEmptyFieldValidator
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import mr.shtein.buddyandroidclient.viewmodels.RegistrationInfoModel
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.Exception
import java.net.SocketTimeoutException

class UserRegistrationFragment : Fragment(R.layout.user_registration_fragment) {

    private val regInfoModel: RegistrationInfoModel by activityViewModels()
    private lateinit var callbackForEmail: MailCallback
    private lateinit var fullEmailValidator: FullEmailValidator
    private lateinit var coroutine: CoroutineScope
    private val retrofitUserRepository: UserRepository by inject()
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val passwordValidator: PasswordEmptyFieldValidator by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val enterSlide = Slide()
        enterSlide.slideEdge = Gravity.RIGHT
        enterSlide.duration = 300
        enterSlide.interpolator = DecelerateInterpolator()
        enterTransition = enterSlide

        val exitSlide = Slide()
        exitSlide.slideEdge = Gravity.LEFT
        exitSlide.duration = 300
        exitSlide.interpolator = DecelerateInterpolator()
        exitTransition = exitSlide
        coroutine = CoroutineScope(Dispatchers.Main)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.let {
            setInsetsListenerForPadding(it, left = false, top = true, right = false, bottom = false)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameInputContainer: TextInputLayout =
            view.findViewById(R.id.registration_name_input_container)
        val emailInputContainer: TextInputLayout =
            view.findViewById(R.id.registration_email_input_container)
        val passwordInputContainer: TextInputLayout =
            view.findViewById(R.id.registration_password_input_container)
        val repeatPasswordInputContainer: TextInputLayout =
            view.findViewById(R.id.registration_repeat_password_input_container)
        val containers: List<TextInputLayout> =
            listOf(
                emailInputContainer, passwordInputContainer,
                repeatPasswordInputContainer, nameInputContainer
            )
        val nameInput: TextInputEditText = view.findViewById(R.id.registration_name_input)
        val emailInput: TextInputEditText = view.findViewById(R.id.registration_email_input)
        val passwordInput: TextInputEditText = view.findViewById(R.id.registration_password_input)
        val repeatPasswordInput: TextInputEditText =
            view.findViewById(R.id.registration_repeat_password_input)

        callbackForEmail = object : MailCallback {
            override fun onSuccess() {
                regInfoModel.email = emailInput.text.toString()
            }

            override fun onFail(error: Exception) {
                emailInputContainer.error = error.message
            }

            override fun onFailure() {
                Toast.makeText(
                    requireContext(),
                    FullEmailValidator.NO_INTERNET_MSG,
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onNoAuthorize() {
                Toast.makeText(
                    requireContext(),
                    FullEmailValidator.NO_AUTHORIZE_MSG,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val nameValidator = NameValidator(nameInput, nameInputContainer)
        fullEmailValidator = FullEmailValidator(
            EmailCheckRequest(emailInput.text.toString()), callbackForEmail, null
        )

        nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                try {
                    nameValidator.nameChecker()
                    regInfoModel.isCheckedName = true
                } catch (ex: TooShortLengthException) {
                    nameInputContainer.isErrorEnabled = true
                    nameInputContainer.error = ex.message
                }
            } else if (hasFocus && nameInputContainer.isErrorEnabled) {
                nameInputContainer.isErrorEnabled = false
            }

        }

        emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val emailCheckRequest = EmailCheckRequest(emailInput.text.toString())
                fullEmailValidator = FullEmailValidator(emailCheckRequest, callbackForEmail, null)
                fullEmailValidator.emailChecker(emailInput, emailInputContainer)
                regInfoModel.isCheckedEmail = true
            } else if (hasFocus && emailInputContainer.isErrorEnabled) {
                emailInputContainer.isErrorEnabled = false
            }

        }

        passwordInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                passwordChecker(passwordInput, passwordInputContainer, passwordValidator)
                regInfoModel.isCheckedPassword = true
            } else if (hasFocus && passwordInputContainer.isErrorEnabled) {
                passwordInputContainer.isErrorEnabled = false
            }
        }

        repeatPasswordInput.setOnFocusChangeListener { item, hasFocus ->
            if (!hasFocus) {
                repeatPasswordChecker(
                    repeatPasswordInput,
                    repeatPasswordInputContainer,
                    passwordInput,
                    passwordValidator,
                    regInfoModel
                )
            } else if (hasFocus && repeatPasswordInputContainer.isErrorEnabled) {
                repeatPasswordInputContainer.isErrorEnabled = false
            }
        }

        view.findViewById<Button>(R.id.registration_kennel_name_fragment_button)
            .setOnClickListener {
                val container: TextInputLayout? =
                    finalErrorsCheck(
                        containers,
                        view,
                        fullEmailValidator,
                        passwordValidator,
                        nameValidator
                    )
                if (container == null) {
                    val cityInfo = userPropertiesRepository.getUserCity()
                    val newUser =
                        Person(
                            emailInput.text.toString(),
                            passwordInput.text.toString(),
                            nameInput.text.toString(),
                            cityInfo
                        )
                    val progressBar = view.findViewById<ProgressBar>(R.id.registration_progress)
                    progressBar.visibility = View.VISIBLE

                    signUpUser(newUser)
                } else {
                    val invalidFieldMsg = Toast.makeText(
                        context,
                        container.error,
                        Toast.LENGTH_LONG
                    )
                    invalidFieldMsg.setGravity(Gravity.BOTTOM, 0, 0)
                    invalidFieldMsg.show()

                    container.clearFocus()
                }
            }
    }

    private fun signUpUser(person: Person) {
        coroutine.launch {
            try {
                retrofitUserRepository.signUp(person)
                val bundle: Bundle = Bundle()
                bundle.putBoolean("is_from_registration", true)
                findNavController().navigate(
                    R.id.action_userRegistrationFragment_to_loginFragment,
                    bundle
                )
            } catch (ex: IncorrectDataException) {
                val exText = getString(R.string.bad_data_text)
                Toast.makeText(requireContext(), exText, Toast.LENGTH_LONG).show()
            } catch (ex: ServerErrorException) {
                val serverErrorMsg = getString(R.string.server_error_msg)
                Toast.makeText(requireContext(), serverErrorMsg, Toast.LENGTH_LONG).show()
            } catch (ex: SocketTimeoutException) {
                val noInternetMsg = getString(R.string.internet_failure_text)
                Toast.makeText(requireContext(), noInternetMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        regInfoModel.isCheckedEmail = false
        regInfoModel.isCheckedPassword = false
        regInfoModel.isCheckedRepeatPassword = false
    }

    private fun finalErrorsCheck(
        inputContainersList: List<TextInputLayout>,
        view: View,
        fullEmailValidator: FullEmailValidator,
        passwordValidator: PasswordEmptyFieldValidator,
        nameValidator: NameValidator
    ): TextInputLayout? {

        val nameInput = view.findViewById<TextInputEditText>(R.id.registration_name_input)
        val nameInputContainer =
            view.findViewById<TextInputLayout>(R.id.registration_name_input_container)

        val emailInput = view.findViewById<TextInputEditText>(R.id.registration_email_input)
        val emailInputContainer =
            view.findViewById<TextInputLayout>(R.id.registration_email_input_container)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.registration_password_input)
        val passwordInputContainer =
            view.findViewById<TextInputLayout>(R.id.registration_password_input_container)
        val repeatPasswordInput =
            view.findViewById<TextInputEditText>(R.id.registration_repeat_password_input)
        val repeatPasswordContainer =
            view.findViewById<TextInputLayout>(R.id.registration_repeat_password_input_container)

        if (!regInfoModel.isCheckedName) {
            try {
                nameValidator.nameChecker()
            } catch (ex: TooShortLengthException) {
                nameInputContainer.isErrorEnabled = true
                nameInputContainer.error = ex.message
            }
        }

        if (!regInfoModel.isCheckedEmail) {
            fullEmailValidator.emailChecker(emailInput, emailInputContainer)
        }
        if (!regInfoModel.isCheckedPassword) {
            passwordChecker(passwordInput, passwordInputContainer, passwordValidator)
        }
        if (!regInfoModel.isCheckedRepeatPassword) {
            repeatPasswordChecker(
                repeatPasswordInput, repeatPasswordContainer,
                passwordInput, passwordValidator, regInfoModel
            )
        }
        for (container: TextInputLayout in inputContainersList) {
            if (container.isErrorEnabled) return container
        }
        return null
    }


}

private fun passwordChecker(
    input: TextInputEditText,
    inputContainer: TextInputLayout,
    passwordValidator: PasswordEmptyFieldValidator
) {
    try {
        val value = input.text.toString()
        passwordValidator.assertIsValidPassword(value)
    } catch (error: EmptyFieldException) {
        inputContainer.error = error.message
    } catch (error: TooShortLengthException) {
        inputContainer.error = error.message
    }
}

private fun repeatPasswordChecker(
    input: TextInputEditText, inputContainer: TextInputLayout,
    pswInput: TextInputEditText,
    passwordValidator: PasswordEmptyFieldValidator,
    regInfoModel: RegistrationInfoModel
) {
    try {
        val value = input.text.toString()
        passwordValidator.assertIsValidRepeatPassword(value, pswInput)
        regInfoModel.isCheckedRepeatPassword = true
    } catch (error: EmptyFieldException) {
        inputContainer.error = error.message
    } catch (error: PasswordsIsDifferentException) {
        inputContainer.error = error.message
    }
}







