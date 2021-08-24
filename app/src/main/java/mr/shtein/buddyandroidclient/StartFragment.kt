package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.airbnb.lottie.LottieAnimationView
import java.util.*

class StartFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.start_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                3 / 2
            }
            override fun onFinish() {
                findNavController().navigate(R.id.welcomeDescriptionFragment, null,
                    navOptions { // Use the Kotlin DSL for building NavOptions
                        anim {
                            enter = android.R.anim.fade_in
                            exit = android.R.anim.fade_out
                        }
                    })
            }
        }
            .start()

    }

    override fun onStart() {
        super.onStart()
    }
}