package mr.shtein.buddyandroidclient

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mr.shtein.buddyandroidclient.exceptions.validate.*
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.viewmodels.RegistrationInfoModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRegistrationFragment : Fragment() {

    private val regInfoModel: RegistrationInfoModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_registration_layout, container, false)
    }

//    override fun onResume() {
//        super.onResume()
//        val emailInput: TextInputEditText = this.findViewById(R.id.registration_email_input)
//        emailInput.requestFocus()
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val emailInput: TextInputEditText = view.findViewById(R.id.registration_email_input)
        val passwordInput: TextInputEditText = view.findViewById(R.id.registration_password_input)
        val repeatPasswordInput: TextInputEditText =
            view.findViewById(R.id.registration_repeat_password_input)


        emailInput.setOnFocusChangeListener { input, hasFocus ->
            val value: TextInputEditText = input as TextInputEditText
            val inputContainer: TextInputLayout = value.parent.parent as TextInputLayout
            if (!hasFocus) {
                try {
                    val value = value.text.toString()
                    assertIsNotEmptyField(value)
                    assertIsValidEmail(value)
                    isEmailExists(value, emailInput)
                } catch (error: EmptyFieldException) {
                    inputContainer.error = error.message
                } catch (error: IllegalEmailException) {
                    inputContainer.error = error.message
                } catch (error: ExistedEmailException) {
                    inputContainer.error = error.message
                }
            } else if (hasFocus && inputContainer.isErrorEnabled) {
                inputContainer.isErrorEnabled = false
            }

        }


        view.findViewById<Button>(R.id.registration_kennel_name_fragment_button)
            .setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.windowInsetsController?.hide(WindowInsetsCompat.Type.ime())

                    if (regInfoModel.checkKennelInfo()) {
                        findNavController().navigate(R.id.action_registrationKennelInfoFragment_to_registrationSecretDataInfoFragment)
                    } else {
                        Toast.makeText(
                            context,
                            "Для продолжения необходимо заполнить все поля",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }

                }
            }

    }

    private fun isEmailExists(email: String, emailInput: TextInputEditText) {
        val emailService = Common.retrofitService
        emailService.isEmailExists(email)
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    val value: Boolean? = response.body()
                    try {
                        assertIsEmailAlreadyExists(value ?: false)
                        regInfoModel.email = email
                    } catch (error: ExistedEmailException) {
                        val containerLayout = emailInput.parent.parent as TextInputLayout
                        containerLayout.error = error.message
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Toast.makeText(emailInput.context, NO_INTERNET_MSG, Toast.LENGTH_LONG).show()
                }

            })
    }

    companion object RegistrationFormValidator {

        private const val EMPTY_FIELD_MSG: String = "Необходимо заполнить поле"
        private const val TOO_SHORT_LENGTH_MSG: String = "Название слишком короткое"
        private const val INVALID_EMAIL_MSG: String = "Вы ввели некорректный email"
        private const val PASSWORD_IS_DIFFERENT_MSG: String = "Пароли не совпадают"
        private const val EXISTED_EMAIL_MSG: String = "Пользователь с таким email уже существует"

//        fun validate(inputText: TextInputEditText) {
//            val value = inputText.text.toString()
//            when (inputText.id) {
//
//                R.id.kennel_name_input -> {
//                    assertIsValidKennelName(value)
//                }
//                R.id.registration_email_input -> {
//                    assertIsValidEmail(value)
//                }
//                R.id.registration_password_input -> {
//                    assertIsValidPassword(value)
//                }
//                R.id.registration_repeat_password_input -> {
//                    assertIsValidRepeatPassword(value, inputText)
//                }
//            }
//        }

        private fun assertIsNotEmptyField(value: String): Boolean {
            if (value.isEmpty()) {
                throw EmptyFieldException(EMPTY_FIELD_MSG)
            }
            if (value.length <= 1) {
                throw TooShortLengthException(TOO_SHORT_LENGTH_MSG)
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
            if (value.length <= 5) throw TooShortLengthException(TOO_SHORT_LENGTH_MSG)
        }

        private fun assertIsValidRepeatPassword(value: String, input: TextInputEditText) {
            if (value.isEmpty()) throw EmptyFieldException(EMPTY_FIELD_MSG)

            val inputContainer: TextInputLayout = input.parent as TextInputLayout
            val passwordInput =
                inputContainer.findViewById<TextInputEditText>(R.id.registration_password_input)
            val password: String = passwordInput.text.toString()

            if (password != value) throw PasswordsIsDifferentException(PASSWORD_IS_DIFFERENT_MSG)
        }



        private const val NO_INTERNET_MSG = "К сожалению интернет не работает"
    }

//    private fun setOnFocusedListenerForInputs(inputSet: Array<TextInputEditText>) {
//        inputSet.forEach { input ->
//            input.setOnFocusChangeListener { v, hasFocus ->
//                if (hasFocus) {
//                    Log.d("COLOR", "It's focused")
//                    v.backgroundTintList = ColorStateList(R.color.black)
//                } else {
//                    Log.d("COLOR", "It's not focused")
//                    v.setBackgroundColor(v.context.getColor(R.color.black))
//                }
//
//            }
//        }
//
//    }

}

