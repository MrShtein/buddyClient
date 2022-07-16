package mr.shtein.buddyandroidclient.screens

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.setStatusBarColor
import mr.shtein.buddyandroidclient.utils.SharedPreferences


class SplashScreenFragment : Fragment(R.layout.start_fragment) {

    private var isInsetsWorked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    ): View {
        exitTransition
        setStatusBarColor(false)
        val view = super.onCreateView(inflater, container, savedInstanceState)

        when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                view?.setBackgroundColor(requireContext().getColor(R.color.cian5_10_percent))
            }
        }
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

                findNavController().navigate(R.id.action_startFragment_to_animalsListFragment)
            }
        }, 3000)

        return view
    }
}