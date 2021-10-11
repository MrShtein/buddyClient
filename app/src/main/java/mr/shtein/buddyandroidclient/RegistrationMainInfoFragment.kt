package mr.shtein.buddyandroidclient

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import mr.shtein.buddyandroidclient.viewmodels.RegistrationInfoModel

class RegistrationMainInfoFragment : Fragment() {

    private val regInfoModel: RegistrationInfoModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.registration_main_info_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val kennelNameInput: TextInputEditText = view.findViewById(R.id.kennel_name_input)
        val emailInput: TextInputEditText = view.findViewById(R.id.registration_email_input)
        val passwordInput: TextInputEditText = view.findViewById(R.id.registration_password_input)
        val repeatPasswordInput: TextInputEditText = view.findViewById(R.id.registration_repeat_password_input)

        kennelNameInput.requestFocus()


        view.findViewById<Button>(R.id.registration_kennel_name_fragment_button).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.windowInsetsController?.hide(WindowInsetsCompat.Type.ime())

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

