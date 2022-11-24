package mr.shtein.buddyandroidclient.screens

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.databinding.ResetPasswordFragmentBinding
import mr.shtein.buddyandroidclient.setInsetsListenerForPadding
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.repository.UserRepository
import org.koin.android.ext.android.inject
import java.net.ConnectException
import java.net.SocketTimeoutException

private const val LOGIN_REQUEST_KEY = "from_reset_password"
private const val MSG_FOR_LOGIN_FRAGMENT_KEY = "message_for_login"

class ResetPasswordFragment : Fragment(R.layout.reset_password_fragment) {

    private var _binding: ResetPasswordFragmentBinding? = null
    private val binding get() = _binding!!
    private val retrofitUserRepository: UserRepository by inject()

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

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ResetPasswordFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        setInsetsListenerForPadding(view, left = false, top = true, right = false, bottom = false)

        setListeners()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setListeners() {

        binding.resetPasswordSendBtn.setOnClickListener {
            resetPassword()
        }

    }

    private fun resetPassword() {
        showProgressBar()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val email = binding.resetPasswordEmailText.text.toString()
                val result = retrofitUserRepository.resetPassword(email)
                if (result != null) {
                    val msgForLoginFragment = getString(R.string.reset_password_success_phrase)
                    setFragmentResult(
                        LOGIN_REQUEST_KEY,
                        bundleOf(MSG_FOR_LOGIN_FRAGMENT_KEY to result)
                    )
                    findNavController().popBackStack()
                } else {
                    val exText = getString(R.string.server_unavailable_msg)
                    setFragmentResult(
                        LOGIN_REQUEST_KEY,
                        bundleOf(MSG_FOR_LOGIN_FRAGMENT_KEY to exText)
                    )
                    findNavController().popBackStack()
                }
            } catch (ex: ConnectException) {
                val exText = getString(R.string.server_unavailable_msg)
                setFragmentResult(
                    LOGIN_REQUEST_KEY,
                    bundleOf(MSG_FOR_LOGIN_FRAGMENT_KEY to exText)
                )
                findNavController().popBackStack()
            } catch (ex: SocketTimeoutException) {
                val exText = getString(R.string.server_unavailable_msg)
                setFragmentResult(
                    LOGIN_REQUEST_KEY,
                    bundleOf(MSG_FOR_LOGIN_FRAGMENT_KEY to exText)
                )
                findNavController().popBackStack()
            } catch (ex: ServerErrorException) {
                setFragmentResult(
                    LOGIN_REQUEST_KEY,
                    bundleOf(MSG_FOR_LOGIN_FRAGMENT_KEY to ex.message)
                )
                findNavController().popBackStack()
            }
        }
    }

    private fun showProgressBar() {
        binding.resetPasswordProgressBar.visibility = View.VISIBLE
        binding.resetPasswordSendBtn.text = ""
        binding.resetPasswordSendBtn.isEnabled = false
    }
}