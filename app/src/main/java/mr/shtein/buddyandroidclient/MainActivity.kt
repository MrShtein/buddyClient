package mr.shtein.buddyandroidclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import mr.shtein.buddyandroidclient.viewmodels.RegistrationInfoModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val regModel: RegistrationInfoModel by viewModels()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        bottomNav.setupWithNavController(navController)

//        navController.addOnDestinationChangedListener { _, _, arguments ->
//            bottomNav.isVisible = arguments?.getBoolean("ShowAppBar", false) ?: true
//        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.userProfileFragment
                || destination.id == R.id.animal_choice_fragment
            ) {
                bottomNav.visibility = View.VISIBLE
            } else {
                bottomNav.visibility = View.GONE
            }
        }


    }

}