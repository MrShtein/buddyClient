package mr.shtein.buddyandroidclient

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.airbnb.lottie.LottieAnimationView
import java.util.*

class WelcomeDescriptionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.welcome_description_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val welcomeText = view.findViewById<TextView>(R.id.welcome_text).apply {
            val str: String = getString(R.string.welcome_text)
            val span = SpannableStringBuilder(str)
            span.setSpan(
                StyleSpan(Typeface.BOLD),
                str.indexOf("BUDDY!"),
                str.indexOf("!"),
                SpannableString.SPAN_INCLUSIVE_INCLUSIVE
            )
            text = span
        }
        val cloudTop = view.findViewById<LottieAnimationView>(R.id.cloud_top)
        val cloudBottom = view.findViewById<LottieAnimationView>(R.id.cloud_bottom)

        val textScaleX = ObjectAnimator.ofFloat(welcomeText, "scaleX", 1f).apply {
            duration = 500
        }
        val textScaleY = ObjectAnimator.ofFloat(welcomeText, "scaleY", 1f).apply {
            duration = 500
        }

        val alphaForTextAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0f).apply {
            duration = 500
        }

        val alphaForCloudAnimation = ObjectAnimator.ofFloat(cloudTop, "alpha", 0f).apply {
            duration = 500
        }
        val alphaForCloudBottomAnimation = ObjectAnimator.ofFloat(cloudBottom, "alpha", 0f).apply {
            duration = 500
        }


        AnimatorSet().apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    findNavController().navigate(R.id.pre_city_choice_fragment)
                }
            })
            play(textScaleX)
                .with(textScaleY)
            play(alphaForTextAnimation)
                .with(alphaForCloudAnimation)
                .with(alphaForCloudBottomAnimation)
                .after(3000)
            start()
        }


    }

    override fun onStart() {
        super.onStart()

    }
}