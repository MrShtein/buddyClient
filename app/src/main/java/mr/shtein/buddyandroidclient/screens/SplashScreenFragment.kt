package mr.shtein.buddyandroidclient.screens

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import mr.shtein.buddyandroidclient.R


class SplashScreenFragment : Fragment(R.layout.start_fragment) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                3 / 2
            }

            override fun onFinish() {
                findNavController().navigate(R.id.action_startFragment_to_cityChoiceFragment)
            }
        }
            .start()
    }
}