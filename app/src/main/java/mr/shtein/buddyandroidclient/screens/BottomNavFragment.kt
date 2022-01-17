package mr.shtein.buddyandroidclient.screens

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import mr.shtein.buddyandroidclient.R

class BottomNavFragment : Fragment(R.layout.bottom_nav_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.bottom_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav: BottomNavigationView = view.findViewById(R.id.bottom_nav_view)


        bottomNav.setupWithNavController(navController)

        bottomNav.menu.findItem(R.id.userProfileFragment).setOnMenuItemClickListener {
            val n = 3
            if (n == 3) {
            createAndShowBottomSheetDialog(bottomNav)
            } else {
                NavigationUI.onNavDestinationSelected(it, navController)
            }
            true
        }


    }

    private fun findRootNavController(): NavController {
        val navHost = activity?.supportFragmentManager?.findFragmentById(R.id.main_host_fragment) as NavHostFragment
        return navHost.navController
    }

    private fun createAndShowBottomSheetDialog(view: View) {
        val bottomSheetDialog = BottomSheetDialog(view.context, R.style.myst)
        bottomSheetDialog.setContentView(R.layout.signup_and_signin_bottom_sheet)
        val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet)

        val toRegistrationButton: Button? = bottomSheet?.findViewById(R.id.to_registration_fragment_button)
        with(toRegistrationButton) {
            this?.setOnClickListener {
                bottomSheetDialog.dismiss()
                findRootNavController().navigate(R.id.action_bottomNavFragment_to_userRegistrationFragment)
            }
        }

        val toLoginFragmentButton: Button? = bottomSheet?.findViewById(R.id.to_login_fragment_button)
        with(toLoginFragmentButton) {
            this?.setOnClickListener {
                bottomSheetDialog.dismiss()
                findRootNavController().navigate(R.id.action_bottomNavFragment_to_loginFragment)
            }
        }

        bottomSheet?.setBackgroundResource(R.color.transparent)

        bottomSheetDialog.show()
    }




}