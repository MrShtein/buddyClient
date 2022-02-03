package mr.shtein.buddyandroidclient.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.utils.SharedPreferences


class SplashScreenFragment : Fragment(R.layout.start_fragment) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)

        view.postDelayed({
            if (storage.readString(SharedPreferences.USER_CITY_KEY, "") == "") {
                findNavController().navigate(R.id.action_startFragment_to_cityChoiceFragment)
            } else {
                findNavController().navigate(R.id.action_startFragment_to_bottomNavFragment2)
            }
        }, 3000)

    }
}