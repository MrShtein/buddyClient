package mr.shtein.buddyandroidclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import mr.shtein.buddyandroidclient.viewmodels.RegistrationInfoModel

class MainActivity : AppCompatActivity() {

    val test: String = "ok"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val regModel: RegistrationInfoModel by viewModels()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        bottomNav.setupWithNavController(navController)

//        bottomNav.menu.findItem(R.id.userProfileFragment).setOnMenuItemClickListener {
//            if (navController.currentDestination?.id == R.id. animal_list_fragment && test == "ok") {
//                navController.navigate(R.id.authFragment)
//            } else {
//                navController.navigate(R.id.userProfileFragment)
//            }
//            true
//        }

//        navController.addOnDestinationChangedListener { _, _, arguments ->
//            bottomNav.isVisible = arguments?.getBoolean("ShowAppBar", false) ?: true
//        }



        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id == R.id.userProfileFragment) {
                if (test == "ok") {
                    Log.d("TEST", "It's work")
                    navController.navigate(R.id.authFragment)
                    bottomNav.visibility = View.GONE
                } else {
                    bottomNav.visibility = View.VISIBLE
                }

            } else if ( destination.id == R.id.animal_list_fragment) {
                bottomNav.visibility = View.VISIBLE
            } else {
                bottomNav.visibility = View.GONE
            }
        }
    }
}