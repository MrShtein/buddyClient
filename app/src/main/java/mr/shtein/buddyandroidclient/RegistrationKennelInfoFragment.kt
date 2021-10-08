package mr.shtein.buddyandroidclient

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import mr.shtein.buddyandroidclient.viewmodels.RegistrationInfoModel

class RegistrationKennelInfoFragment : Fragment() {

    private val regInfoModel: RegistrationInfoModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration_kennel_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val kennelNameInput: EditText = view.findViewById(R.id.kennel_name_input)
        val cityNameInput: EditText = view.findViewById(R.id.registration_city_name_input)
        val streetNameInput: EditText = view.findViewById(R.id.registration_street_name_input)
        val houseNumberInput: EditText = view.findViewById(R.id.registration_house_number_input)
        val phoneNumberInput: EditText = view.findViewById(R.id.registration_phone_number_input)

        view.findViewById<Button>(R.id.registration_kennel_name_fragment_button).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.windowInsetsController?.hide(WindowInsetsCompat.Type.ime())

                with(regInfoModel) {
                    kennelName = kennelNameInput.text.toString()
                    city = cityNameInput.text.toString()
                    street = streetNameInput.text.toString()
                    house = houseNumberInput.text.toString()
                    phoneNumber = phoneNumberInput.text.toString()
                }

                if (regInfoModel.checkKennelInfo()) {
                    findNavController().navigate(R.id.action_registrationKennelInfoFragment_to_registrationSecretDataInfoFragment)
                } else {
                    Toast.makeText(
                        context, "Для продолжения необходимо заполнить все поля", Toast.LENGTH_LONG
                    )
                        .show()
                }

            }
        }




    }

}

