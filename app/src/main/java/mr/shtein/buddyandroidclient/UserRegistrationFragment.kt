package mr.shtein.buddyandroidclient

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mr.shtein.buddyandroidclient.exceptions.validate.*
import mr.shtein.buddyandroidclient.model.User
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.viewmodels.RegistrationInfoModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class UserRegistrationFragment : Fragment() {

    private val regInfoModel: RegistrationInfoModel by activityViewModels()
    private var isRegistered: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_registration_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val emailInputContainer: TextInputLayout =
            view.findViewById(R.id.registration_email_input_container)
        val passwordInputContainer: TextInputLayout =
            view.findViewById(R.id.registration_password_input_container)
        val repeatPasswordInputContainer: TextInputLayout =
            view.findViewById(R.id.registration_repeat_password_input_container)
        val containers: List<TextInputLayout> =
            listOf(emailInputContainer, passwordInputContainer, repeatPasswordInputContainer)
        val emailInput: TextInputEditText = view.findViewById(R.id.registration_email_input)
        val passwordInput: TextInputEditText = view.findViewById(R.id.registration_password_input)
        val repeatPasswordInput: TextInputEditText =
            view.findViewById(R.id.registration_repeat_password_input)


        emailInput.setOnFocusChangeListener { item, hasFocus ->
            if (!hasFocus) {
                emailChecker(emailInput, emailInputContainer)
                regInfoModel.isCheckedEmail = true
            } else if (hasFocus && emailInputContainer.isErrorEnabled) {
                emailInputContainer.isErrorEnabled = false
            }

        }

        passwordInput.setOnFocusChangeListener { item, hasFocus ->
            if (!hasFocus) {
                passwordChecker(passwordInput, passwordInputContainer)
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
                    passwordInput
                )
            } else if (hasFocus && repeatPasswordInputContainer.isErrorEnabled) {
                repeatPasswordInputContainer.isErrorEnabled = false
            }
        }

        view.findViewById<Button>(R.id.registration_kennel_name_fragment_button)
            .setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.windowInsetsController?.hide(WindowInsetsCompat.Type.ime())
                    val container: TextInputLayout? = finalErrorsCheck(containers, view)
                    if (container == null) {
                        val frame: FrameLayout = view.findViewById(R.id.registration_frame)
                        val startAnimValue: Int =
                            activity?.getColor(R.color.black_with_transparency_0) ?: 0
                        val endAnimValue: Int =
                            activity?.getColor(R.color.black_with_transparency_50) ?: 0
                        animateFrame(startAnimValue, endAnimValue, frame)
                        val newUser =
                            User(emailInput.text.toString(), passwordInput.text.toString())
                        val progressBar = view.findViewById<ProgressBar>(R.id.registration_progress)
                        progressBar.visibility = View.VISIBLE
                        object : CountDownTimer(10000, 1000) {
                            val startAnimReverseValue =
                                activity?.getColor(R.color.black_with_transparency_50) ?: 0
                            val endAnimReverseValue =
                                activity?.getColor(R.color.black_with_transparency_0) ?: 0

                            override fun onTick(millisUntilFinished: Long) {

                                if (isRegistered) {
                                    val bundle: Bundle = Bundle()
                                    bundle.putBoolean("is_from_registration", true)
                                    findNavController().navigate(R.id.action_userRegistrationFragment_to_loginFragment, bundle)
                                    cancel()
                                }
                            }

                            override fun onFinish() {
                                progressBar.visibility = View.INVISIBLE
                                animateFrame(startAnimReverseValue, endAnimReverseValue, frame)
                                MaterialAlertDialogBuilder(requireContext())
                                    .setMessage("Что-то пошло не так, попробуйте позже")
                                    .setPositiveButton("Ok") {
                                            dialog, _ -> dialog?.cancel()
                                    }
                                    .show()
                            }
                        }.start()

                        val retrofitService = Common.retrofitService
                        retrofitService.registerUser(newUser)
                            .enqueue(object : Callback<Boolean> {
                                override fun onResponse(
                                    call: Call<Boolean>,
                                    response: Response<Boolean>
                                ) {
                                    if (response.body() == true) {
                                        isRegistered = true
                                    }
                                }

                                override fun onFailure(
                                    call: Call<Boolean>,
                                    t: Throwable
                                ) {
                                }
                            })


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

    }

    override fun onResume() {
        super.onResume()
        regInfoModel.isCheckedEmail = false
        regInfoModel.isCheckedPassword = false
        regInfoModel.isCheckedRepeatPassword = false
    }

    private fun animateFrame(startValue: Int, endValue: Int, animatedElement: View) {
        val animator: ValueAnimator = ObjectAnimator.ofObject(ArgbEvaluator(), startValue, endValue)
        animator.duration = 250
        animator.addUpdateListener {
            animatedElement.setBackgroundColor(animator.animatedValue as Int)
        }
        animator.start()
    }

    private fun finalErrorsCheck(
        inputContainersList: List<TextInputLayout>,
        view: View
    ): TextInputLayout? {

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

        if (!regInfoModel.isCheckedEmail) {
            emailChecker(emailInput, emailInputContainer)
        }
        if (!regInfoModel.isCheckedPassword) {
            passwordChecker(passwordInput, passwordInputContainer)
        }
        if (!regInfoModel.isCheckedRepeatPassword) {
            repeatPasswordChecker(repeatPasswordInput, repeatPasswordContainer, passwordInput)
        }
        for (container: TextInputLayout in inputContainersList) {
            if (container.isErrorEnabled) return container
        }
        return null
    }

    private fun emailChecker(input: TextInputEditText, inputContainer: TextInputLayout) {
        try {
            val value = input.text.toString()
            assertIsNotEmptyField(value)
            assertIsValidEmail(value)
            isEmailExists(value, object : MailCallback {
                override fun onSuccess() {
                    regInfoModel.email = value
                }

                override fun onFail(error: Exception) {
                    val containerLayout = input.parent.parent as TextInputLayout
                    containerLayout.error = error.message
                }

                override fun onFailure() {
                    Toast.makeText(context, NO_INTERNET_MSG, Toast.LENGTH_LONG).show()
                }
            })
        } catch (error: EmptyFieldException) {
            inputContainer.error = error.message
        } catch (error: IllegalEmailException) {
            inputContainer.error = error.message
        } catch (error: ExistedEmailException) {
            inputContainer.error = error.message
        } catch (error: TooShortLengthException) {
            inputContainer.error = error.message
        }
    }

    private fun passwordChecker(input: TextInputEditText, inputContainer: TextInputLayout) {
        try {
            val value = input.text.toString()
            assertIsValidPassword(value)
        } catch (error: EmptyFieldException) {
            inputContainer.error = error.message
        } catch (error: TooShortLengthException) {
            inputContainer.error = error.message
        }
    }

    private fun repeatPasswordChecker(
        input: TextInputEditText, inputContainer: TextInputLayout,
        pswInput: TextInputEditText
    ) {
        try {
            val value = input.text.toString()
            assertIsValidRepeatPassword(value, pswInput)
            regInfoModel.isCheckedRepeatPassword = true
        } catch (error: EmptyFieldException) {
            inputContainer.error = error.message
        } catch (error: PasswordsIsDifferentException) {
            inputContainer.error = error.message
        }
    }

    interface MailCallback {
        fun onSuccess()
        fun onFail(error: Exception)
        fun onFailure()
    }


    private fun isEmailExists(email: String, callback: MailCallback) {
        val emailService = Common.retrofitService
        emailService.isEmailExists(email)
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    val value: Boolean? = response.body()
                    try {
                        assertIsEmailAlreadyExists(value ?: false)
                        callback.onSuccess()
                    } catch (error: ExistedEmailException) {
                        callback.onFail(error)
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    callback.onFailure()
                }
            })
    }

    companion object RegistrationFormValidator {

        private const val EMPTY_FIELD_MSG: String = "Необходимо заполнить поле"
        private const val TOO_SHORT_PASSWORD_MSG: String = "Пароль слишком короткий"
        private const val INVALID_EMAIL_MSG: String = "Вы ввели некорректный email"
        private const val PASSWORD_IS_DIFFERENT_MSG: String = "Пароли не совпадают"
        private const val EXISTED_EMAIL_MSG: String = "Пользователь с таким email уже существует"
        private const val NO_INTERNET_MSG = "К сожалению интернет не работает"

        private fun assertIsNotEmptyField(value: String): Boolean {
            if (value.isBlank()) {
                throw EmptyFieldException(EMPTY_FIELD_MSG)
            }
            return true
        }

        private fun assertIsValidEmail(value: String): Boolean {
            val regexForEmail: Regex =
                Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
            if (!regexForEmail.matches(value)) throw IllegalEmailException(INVALID_EMAIL_MSG)
            return true
        }

        private fun assertIsEmailAlreadyExists(value: Boolean): Boolean {
            if (value) throw ExistedEmailException(EXISTED_EMAIL_MSG)
            return false
        }

        private fun assertIsValidPassword(value: String) {
            if (value.isEmpty()) throw EmptyFieldException(EMPTY_FIELD_MSG)
            if (value.length <= 5) throw TooShortLengthException(TOO_SHORT_PASSWORD_MSG)
        }

        private fun assertIsValidRepeatPassword(value: String, passwordInput: TextInputEditText) {
            if (value.isEmpty()) throw EmptyFieldException(EMPTY_FIELD_MSG)
            val password: String = passwordInput.text.toString()
            if (password != value) throw PasswordsIsDifferentException(PASSWORD_IS_DIFFERENT_MSG)
        }
    }
}

