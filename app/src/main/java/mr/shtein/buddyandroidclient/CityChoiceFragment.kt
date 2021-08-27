package mr.shtein.buddyandroidclient

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import java.util.*

class CityChoiceFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.city_choice_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textForCityChoice = view.findViewById<TextView>(R.id.cityChoiceText)
        val nextButton = view.findViewById<Button>(R.id.nextButton)

        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f)

        val textScale = ObjectAnimator.ofPropertyValuesHolder(textForCityChoice, scaleX, scaleY)
        val buttonScale = ObjectAnimator.ofPropertyValuesHolder(nextButton, scaleX, scaleY)

        AnimatorSet().apply {
            duration = 500
            play(textScale).with(buttonScale)

            start()
        }
    }

    override fun onStart() {
        super.onStart()
    }
}