package mr.shtein.buddyandroidclient.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import mr.shtein.buddyandroidclient.R

class BottomNavFragment : Fragment(R.layout.bottom_nav_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.bottom_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav: BottomNavigationView = view.findViewById(R.id.bottom_nav_view)
        bottomNav.setupWithNavController(navController)


//        bottomNav.menu.findItem(R.id.userProfileFragment).setOnMenuItemClickListener {
//            var n = 3
//
//            if (n == 3) {
//            showBottomSheetDialog(bottomNav)
//            //Toast.makeText(this.baseContext, ";lkasjdf", Toast.LENGTH_LONG).show()
//            } else {
//                NavigationUI.onNavDestinationSelected(it, navController)
//            }
//            true
//        }
    }
}