package mr.shtein.buddyandroidclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import mr.shtein.buddyandroidclient.viewmodels.RegistrationInfoModel

class RegistrationAdditionalKennelInfoFragment : Fragment() {

    private val regInfoModel: RegistrationInfoModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.registration_additional_kennel_info_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.registration_secret_data_button)
        button.setOnClickListener {
            findNavController().navigate(R.id.action_registrationSecretDataInfoFragment_to_registrationAdditionalInfoFragment2)
        }
    }
}