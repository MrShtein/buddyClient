package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.*

class WelcomeDescriptionFragment: Fragment() {

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
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                3 / 2
            }
            override fun onFinish() {
                findNavController().navigate(R.id.cityChoiceFragment)
            }
        }
            .start()
    }

    override fun onStart() {
        super.onStart()

    }
}