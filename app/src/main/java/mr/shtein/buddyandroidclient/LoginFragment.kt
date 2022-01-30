package mr.shtein.buddyandroidclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import mr.shtein.buddyandroidclient.model.LoginResponse
import mr.shtein.buddyandroidclient.model.Person
import mr.shtein.buddyandroidclient.model.LoginInfo
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var isEmailChecked: Boolean? = null
    private var isPasswordChecked: Boolean? = null

    override fun onStart() {
        super.onStart()
        isEmailChecked = false
        isPasswordChecked = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle: Bundle? = arguments
        val isFromRegistration = bundle?.getBoolean(SharedPreferences.IS_FROM_REGISTRATION_KEY) ?: false
        if (isFromRegistration) {
            Snackbar.make(view, R.string.snackbar_registration_text, Snackbar.LENGTH_LONG)
                .setDuration(3000)
                .show();
        }

        val emailInput: TextInputEditText = view.findViewById(R.id.login_email_input)
        val passwordInput: TextInputEditText = view.findViewById(R.id.login_password_input)
        val button: Button = view.findViewById(R.id.login_button)
        val user = Person("", "", "")

        button.setOnClickListener {

            user.email = emailInput.text.toString()
            user.password = passwordInput.text.toString()

            val retrofitService = Common.retrofitService
            val sharedPropertyStore =
                SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)

            retrofitService.loginUser(user)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val loginResponse = response.body()
                        if (loginResponse?.error == "") {
                            val loginInfo: LoginInfo = loginResponse.loginInfo

                            sharedPropertyStore.writeString(SharedPreferences.TOKEN_KEY, loginInfo.token)
                            sharedPropertyStore.writeLong(SharedPreferences.USER_ID_KEY, loginInfo.id)
                            sharedPropertyStore.writeString(SharedPreferences.USER_LOGIN_KEY, loginInfo.login)
                            sharedPropertyStore.writeString(SharedPreferences.USER_ROLE_KEY, loginInfo.role)
                            sharedPropertyStore.writeBoolean(SharedPreferences.IS_LOCKED_KEY, loginInfo.isLocked)
                            sharedPropertyStore.writeString(SharedPreferences.USER_NAME_KEY, loginInfo.name)
                            sharedPropertyStore.writeString(SharedPreferences.USER_SURNAME_KEY, loginInfo.surname)
                            sharedPropertyStore.writeString(SharedPreferences.USER_PHONE_NUMBER_KEY, loginInfo.phone)

                        findNavController().navigate(R.id.action_loginFragment_to_bottomNavFragment)
                        } else {
                            MaterialAlertDialogBuilder(requireContext())
                                .setMessage("Вы ввели неправильный логин или пароль")
                                .setPositiveButton("Ok") { dialog, _ ->
                                    dialog?.cancel()
                                }
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Что-то пошло не так", Toast.LENGTH_LONG)
                            .show()
                    }
                })
        }
    }
}

