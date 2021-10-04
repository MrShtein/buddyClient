package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ToggleButton

class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val passwordVisibilityBtn = view.findViewById<ToggleButton>(R.id.password_visibility)
        val passwordField = view.findViewById<EditText>(R.id.login_password_input_id)
        passwordVisibilityBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordField.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }
}