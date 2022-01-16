package mr.shtein.buddyandroidclient

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
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
import mr.shtein.buddyandroidclient.utils.EmailValidator
import mr.shtein.buddyandroidclient.utils.PasswordValidator
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
        return inflater.inflate(R.layout.user_registration_fragment, container, false)
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

        val emailValidator = EmailValidator(requireContext(), regInfoModel)
        val passwordValidator = PasswordValidator()


        emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                emailValidator.emailChecker(emailInput, emailInputContainer)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.windowInsetsController?.hide(WindowInsetsCompat.Type.ime())
                    val container: TextInputLayout? =
                        finalErrorsCheck(containers, view, emailValidator, passwordValidator)
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
//                                    findNavController().navigate(
//                                        R.id.action_userRegistrationFragment_to_loginFragment,
//                                        bundle
//                                    )
                                    cancel()
                                }
                            }

                            override fun onFinish() {
                                progressBar.visibility = View.INVISIBLE
                                animateFrame(startAnimReverseValue, endAnimReverseValue, frame)
                                MaterialAlertDialogBuilder(requireContext())
                                    .setMessage("Что-то пошло не так, попробуйте позже")
                                    .setPositiveButton("Ok") { dialog, _ ->
                                        dialog?.cancel()
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
                                    println()
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
        view: View,
        emailValidator: EmailValidator,
        passwordValidator: PasswordValidator
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
            emailValidator.emailChecker(emailInput, emailInputContainer)
        }
        if (!regInfoModel.isCheckedPassword) {
            passwordChecker(passwordInput, passwordInputContainer, passwordValidator)
        }
        if (!regInfoModel.isCheckedRepeatPassword) {
            repeatPasswordChecker(repeatPasswordInput, repeatPasswordContainer,
                passwordInput, passwordValidator, regInfoModel)
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
    passwordValidator: PasswordValidator
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
    passwordValidator: PasswordValidator,
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

interface MailCallback {
    fun onSuccess()
    fun onFail(error: Exception)
    fun onFailure()
}



