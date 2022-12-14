package mr.shtein.auth.presentation

import android.app.Activity
import android.content.Context
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import mr.shtein.auth.R
import mr.shtein.auth.navigation.AuthNavigation

class BottomSheetDialogShower(private val context: Context, private val navigator: AuthNavigation) {

    fun createAndShowBottomSheetDialog() {
        val bottomSheetDialog =
            BottomSheetDialog(context, R.style.Buddy_Widget_BottomSheetDialog_Auth)
        bottomSheetDialog.setContentView(R.layout.signup_and_signin_bottom_sheet)

        val toRegistrationButton: Button? =
            bottomSheetDialog.findViewById(R.id.to_registration_fragment_button)
        with(toRegistrationButton) {
            this?.setOnClickListener {
                bottomSheetDialog.dismiss()
                navigator.moveToUserRegistration()
            }
        }

        val toLoginFragmentButton: Button? =
            bottomSheetDialog.findViewById(R.id.to_login_fragment_button)
        with(toLoginFragmentButton) {
            this?.setOnClickListener {
                bottomSheetDialog.dismiss()
                navigator.moveToLoginFromAnimalList()
            }
        }
        bottomSheetDialog.show()
    }
}