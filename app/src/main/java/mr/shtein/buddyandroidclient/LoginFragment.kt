package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.shtein.buddyandroidclient.exceptions.validate.IncorrectDataException
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.Person
import mr.shtein.buddyandroidclient.model.LoginInfo
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.data.repository.UserRepository
import mr.shtein.buddyandroidclient.utils.FragmentsListForAssigningAnimation
import org.koin.android.ext.android.inject
import java.lang.NullPointerException
import java.net.ConnectException
import java.net.SocketTimeoutException


private const val LAST_FRAGMENT_KEY = "last_fragment"
private const val RESET_REQUEST_KEY = "from_reset_password"
private const val MSG_FROM_RESET_FRAGMENT_KEY = "message_for_login"
private const val IS_FROM_REGISTRATION_KEY = "is_from_registration"

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var isEmailChecked: Boolean? = null
    private var isPasswordChecked: Boolean? = null
    private lateinit var coroutine: CoroutineScope
    private val retrofitUserRepository: UserRepository by inject()
    private val userPropertiesRepository: UserPropertiesRepository by inject()

    override fun onStart() {
        super.onStart()
        isEmailChecked = false
        isPasswordChecked = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coroutine = CoroutineScope(Dispatchers.Main)
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

        setFragmentResultListener(RESET_REQUEST_KEY) { _, bundle ->
            val msg = bundle.getString(MSG_FROM_RESET_FRAGMENT_KEY)
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
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
        val bundle: Bundle? = arguments
        val isFromRegistration =
            bundle?.getBoolean(IS_FROM_REGISTRATION_KEY) ?: false
        if (isFromRegistration) {
            Snackbar.make(view, R.string.snackbar_registration_text, Snackbar.LENGTH_LONG)
                .setDuration(3000)
                .show();
        }

        val emailInput: TextInputEditText = view.findViewById(R.id.login_email_input)
        val passwordInput: TextInputEditText = view.findViewById(R.id.login_password_input)
        val button: Button = view.findViewById(R.id.login_button)
        val user = Person("", "", "", "")
        val forgotPasswordBtn = view.findViewById<Button>(R.id.login_fragment_forgot_password_btn)

        forgotPasswordBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }

        button.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            signIn(user, email, password)
        }
    }

    private fun signIn(user: Person, email: String, password: String) {
        user.email = email
        user.password = password

        val serverErrorMsg = getString(R.string.server_error_msg)

        coroutine.launch {
            try {
                val loginResult = retrofitUserRepository.signIn(user)
                val loginInfo: LoginInfo = loginResult.loginInfo

                userPropertiesRepository.saveUserCity(loginInfo.cityInfo)
                userPropertiesRepository.saveUserToken("Bearer ${loginInfo.token}")
                userPropertiesRepository.saveUserId(loginInfo.id)
                userPropertiesRepository.saveUserLogin(loginInfo.login)
                userPropertiesRepository.saveUserRole(loginInfo.role)
                userPropertiesRepository.saveIsUserLocked(loginInfo.isLocked)
                userPropertiesRepository.saveUserName(loginInfo.name)
                userPropertiesRepository.saveUserSurname(loginInfo.surname)
                userPropertiesRepository.saveUserPhoneNumber(loginInfo.phone)
                userPropertiesRepository.saveUserGender(loginInfo.gender)

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.animalsListFragment, false)
                    .build()
                val lastFragmentBundle = bundleOf()
                lastFragmentBundle.putParcelable(LAST_FRAGMENT_KEY, FragmentsListForAssigningAnimation.LOGIN)
                findNavController().navigate(
                    R.id.action_loginFragment_to_animalsListFragment,
                    lastFragmentBundle,
                    navOptions
                )
            } catch (ex: IncorrectDataException) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Вы ввели неправильный логин или пароль")
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog?.cancel()
                    }
                    .show()
            } catch (ex: ServerErrorException) {
                Toast.makeText(requireContext(), serverErrorMsg, Toast.LENGTH_LONG)
                    .show()
            } catch (ex: NullPointerException) {
                Toast.makeText(requireContext(), serverErrorMsg, Toast.LENGTH_LONG)
                    .show()
            } catch (ex: SocketTimeoutException) {
                val exText = getString(R.string.internet_failure_text)
                Toast.makeText(requireContext(), serverErrorMsg, Toast.LENGTH_LONG)
                    .show()
            } catch (ex: ConnectException) {
                Toast.makeText(requireContext(), serverErrorMsg, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}

