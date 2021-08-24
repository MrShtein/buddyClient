package mr.shtein.buddyandroidclient

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView

const val TAG = "StartFragment"

class StartFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.start_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sun: LottieAnimationView = view.findViewById(R.id.sun_pulse)

        sun.setOnClickListener {

            Log.d(TAG, "Click start")
            val dog = view.findViewById<LottieAnimationView>(R.id.dog)
            val cat = view.findViewById<LottieAnimationView>(R.id.cat)
            val cloudTop = view.findViewById<LottieAnimationView>(R.id.cloud_top)
            val cloudBottom = view.findViewById<LottieAnimationView>(R.id.cloud_bottom)

            val dogOffset: Float = (dog.bottom - dog.top).toFloat()
            val dogAnimator = ObjectAnimator.ofFloat(dog, "translationY", dogOffset).apply {
                duration = 500
            }

            val catOffset: Float = (cat.top - cat.bottom).toFloat()
            val catAnimator = ObjectAnimator.ofFloat(cat, "translationY", catOffset).apply {
                duration = 500
            }

            val cloudTopAnimator = ObjectAnimator.ofFloat(cloudTop, "alpha",1f, 0f).apply {
                duration = 500
            }

            val cloudBottomAnimator = ObjectAnimator.ofFloat(cloudBottom, "alpha",1f, 0f).apply {
                duration = 500
            }
            val sun: LottieAnimationView = view.findViewById(R.id.sun_pulse)
            val sunAnimatorX = ObjectAnimator.ofFloat(sun, "scaleX", 1f, 0f).apply {
                duration = 500
            }
            val sunAnimatorY = ObjectAnimator.ofFloat(sun, "scaleY", 1f, 0f).apply {
                duration = 500
            }

            AnimatorSet().apply {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        findNavController().navigate(R.id.welcomeDescriptionFragment)
                    }
                })
                play(dogAnimator)
                    .with(catAnimator)
                    .with(cloudTopAnimator)
                    .with(sunAnimatorX)
                    .with(sunAnimatorY)
                    .with(cloudBottomAnimator)
                start()
            }

            Log.d(TAG, "Anim start")
        }

//        object : CountDownTimer(3000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                3 / 2
//            }
//            override fun onFinish() {
//                findNavController().navigate(R.id.welcomeDescriptionFragment, null,
//                    navOptions { // Use the Kotlin DSL for building NavOptions
//                        anim {
//                            enter = android.R.anim.fade_in
//                            exit = android.R.anim.fade_out
//                        }
//                    })
//            }
//        }
//            .start()

    }

    override fun onStart() {
        super.onStart()
    }
}