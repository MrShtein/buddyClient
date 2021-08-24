package mr.shtein.buddyandroidclient

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
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
        val welcomeText = view.findViewById<TextView>(R.id.welcome_text)

        val textScaleX = ObjectAnimator.ofFloat(welcomeText, "scaleX", 1f).apply {
            duration = 500
        }
        val textScaleY = ObjectAnimator.ofFloat(welcomeText, "scaleY", 1f).apply {
            duration = 500
        }

        AnimatorSet().apply {
            play(textScaleX).with(textScaleY)
            start()
        }


    }

    override fun onStart() {
        super.onStart()

    }
}