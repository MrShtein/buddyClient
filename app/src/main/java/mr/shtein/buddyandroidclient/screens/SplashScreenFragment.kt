package mr.shtein.buddyandroidclient.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.setStatusBarColor
import mr.shtein.buddyandroidclient.utils.SharedPreferences


class SplashScreenFragment : Fragment(R.layout.start_fragment) {

    private var isInsetsWorked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStatusBarColor(false)
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val logoText: ImageView = view!!.findViewById(R.id.buddy_logo_text)
        ViewCompat.setOnApplyWindowInsetsListener(logoText) { textView, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val oldMargin = logoText.marginBottom
            textView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                if (!isInsetsWorked) bottomMargin = insets.bottom + oldMargin
                isInsetsWorked = true
            }
            WindowInsetsCompat.CONSUMED
        }
        val storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        view.postDelayed({
            isInsetsWorked = false
            if (storage.readString(SharedPreferences.USER_CITY_KEY, "") == "") {
                findNavController().navigate(R.id.action_startFragment_to_cityChoiceFragment)
            } else {
                findNavController().navigate(R.id.action_startFragment_to_bottomNavFragment2)
            }
        }, 3000)

        return view
    }
}