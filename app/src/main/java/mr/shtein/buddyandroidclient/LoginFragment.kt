package mr.shtein.buddyandroidclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import mr.shtein.buddyandroidclient.model.LoginResponse
import mr.shtein.buddyandroidclient.model.User
import mr.shtein.buddyandroidclient.model.UserInfo
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.utils.SharedPreferencesIO
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
        val isFromRegistration = bundle?.getBoolean(SharedPreferencesIO.IS_FROM_REGISTRATION_KEY) ?: false
        if (isFromRegistration) {
            Snackbar.make(view, R.string.snackbar_registration_text, Snackbar.LENGTH_LONG)
                .setDuration(3000)
                .show();
        }

        val emailInput: TextInputEditText = view.findViewById(R.id.login_email_input)
        val passwordInput: TextInputEditText = view.findViewById(R.id.login_password_input)
        val button: Button = view.findViewById(R.id.login_button)
        val user = User("", "", "")

        button.setOnClickListener {

            user.email = emailInput.text.toString()
            user.password = passwordInput.text.toString()

            val retrofitService = Common.retrofitService
            val sharedPropertyStore =
                SharedPreferencesIO(requireContext(), SharedPreferencesIO.PERSISTENT_STORAGE_NAME)

            retrofitService.loginUser(user)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val loginResponse = response.body()
                        if (loginResponse?.error == "") {
                            val userInfo: UserInfo = loginResponse.userInfo

                            sharedPropertyStore.writeString(SharedPreferencesIO.TOKEN_KEY, userInfo.token)
                            sharedPropertyStore.writeLong(SharedPreferencesIO.USER_ID_KEY, userInfo.id)
                            sharedPropertyStore.writeString(SharedPreferencesIO.USER_LOGIN_KEY, userInfo.login)
                            sharedPropertyStore.writeString(SharedPreferencesIO.USER_ROLE_KEY, userInfo.role)
                            sharedPropertyStore.writeBoolean(SharedPreferencesIO.IS_LOCKED_KEY, userInfo.isLocked)
                            sharedPropertyStore.writeString(SharedPreferencesIO.USER_NAME_KEY, userInfo.name)
                            sharedPropertyStore.writeString(SharedPreferencesIO.USER_SURNAME_KEY, userInfo.surname)
                            sharedPropertyStore.writeString(SharedPreferencesIO.USER_PHONE_NUMBER_KEY, userInfo.phone)

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

