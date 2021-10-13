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
import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.exceptions.validate.IllegalEmailException
import mr.shtein.buddyandroidclient.exceptions.validate.PasswordsIsDifferentException
import mr.shtein.buddyandroidclient.exceptions.validate.TooShortLengthException
import mr.shtein.buddyandroidclient.viewmodels.RegistrationInfoModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val emailInput: TextInputEditText = view.findViewById(R.id.registration_email_input)
        val passwordInput: TextInputEditText = view.findViewById(R.id.registration_password_input)
        val repeatPasswordInput: TextInputEditText =
            view.findViewById(R.id.registration_repeat_password_input)

        emailInput.requestFocus()

        emailInput.setOnFocusChangeListener { input, hasFocus ->
            val value: TextInputEditText = input as TextInputEditText
            val inputContainer: TextInputLayout = value.parent.parent as TextInputLayout
            if (!hasFocus) {
                try {
                    assertIsValidKennelName(value.text.toString())
                } catch (error: EmptyFieldException) {
                    inputContainer.error = error.message
                } catch (error: TooShortLengthException) {
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

    companion object RegistrationFormValidator {

        private const val EMPTY_FIELD_MSG: String = "Необходимо заполнить поле"
        private const val TOO_SHORT_LENGTH_MSG: String = "Название слишком короткое"
        private const val INVALID_EMAIL_MSG: String = "Вы ввели несуществующий email"
        private const val PASSWORD_IS_DIFFERENT_MSG: String = "Пароли не совпадают"

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

        private fun assertIsValidKennelName(value: String) {
            if (value.isEmpty()) {
                throw EmptyFieldException(EMPTY_FIELD_MSG)
            }
            if (value.length <= 1) {
                throw TooShortLengthException(TOO_SHORT_LENGTH_MSG)
            }
        }

        private fun assertIsValidEmail(value: String) {
            val regexForEmail: Regex =
                Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
            if (!regexForEmail.matches(value)) throw IllegalEmailException(INVALID_EMAIL_MSG)
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

