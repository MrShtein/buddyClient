package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController

class AuthFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("BACK", "It's work")
                findNavController().popBackStack(R.id.animal_list_fragment, false)
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.login_fragment_start_button).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        view.findViewById<Button>(R.id.registration_fragment_start_button).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userRegistrationFragment)
        }
    }


}