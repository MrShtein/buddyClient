package mr.shtein.buddyandroidclient.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.utils.BottomSheetDialogShower
import mr.shtein.buddyandroidclient.utils.SharedPreferences

class BottomNavFragment : Fragment(R.layout.bottom_nav_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.bottom_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav: BottomNavigationView = view.findViewById(R.id.bottom_nav_view)


        bottomNav.setupWithNavController(navController)

        bottomNav.menu.findItem(R.id.profile_graph).setOnMenuItemClickListener {
            val sharedPreferenceStore =
                SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
            val token: String = sharedPreferenceStore.readString(SharedPreferences.TOKEN_KEY, "")
            if (token == "") {
                BottomSheetDialogShower.createAndShowBottomSheetDialog(bottomNav, this)
            } else {
               NavigationUI.onNavDestinationSelected(it, navController)
            }
            true
        }
    }






}