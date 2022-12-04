package mr.shtein.buddyandroidclient.utils

import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import mr.shtein.buddyandroidclient.R

class BottomSheetDialogShower {

    companion object {

        fun createAndShowBottomSheetDialog(view: View, navController: NavController) {
            val bottomSheetDialog = BottomSheetDialog(view.context, R.style.mySt)
            bottomSheetDialog.setContentView(R.layout.signup_and_signin_bottom_sheet)
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet)

            val toRegistrationButton: Button? =
                bottomSheet?.findViewById(R.id.to_registration_fragment_button)
            with(toRegistrationButton) {
                this?.setOnClickListener {
                    bottomSheetDialog.dismiss()
                    navController.navigate(R.id.action_animalsListFragment_to_userRegistrationFragment)
                }
            }

            val toLoginFragmentButton: Button? =
                bottomSheet?.findViewById(R.id.to_login_fragment_button)
            with(toLoginFragmentButton) {
                this?.setOnClickListener {
                    bottomSheetDialog.dismiss()
                    navController.navigate(R.id.action_animalsListFragment_to_loginFragment)
                }
            }

            bottomSheet?.setBackgroundResource(R.color.transparent)

            bottomSheetDialog.show()
        }
    }
}