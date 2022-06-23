package mr.shtein.buddyandroidclient.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.platform.MaterialSharedAxis
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
        exitTransition
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
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in)
                    .setExitAnim(R.anim.slide_out)
                    .setPopUpTo(R.id.startFragment, true)
                    .build()
                findNavController().navigate(R.id.action_startFragment_to_animalsListFragment, null, navOptions)
            }
        }, 3000)

        return view
    }
}