package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.view.View
import android.view.View.OnLayoutChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.buddyandroidclient.navigation.Navigator
import mr.shtein.auth.presentation.BottomSheetDialogShower
import mr.shtein.ui_util.FragmentsListForAssigningAnimation
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.util.CommonViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val USER_PROFILE_LABEL = "UserProfileFragment"
private const val ANIMAL_LIST_LABEL = "animals_list_fragment"
private const val ADD_KENNEL_LABEL = "AddKennelFragment"

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val navigator: Navigator by inject()
    private val bottomSheetDialogShower: BottomSheetDialogShower by inject {
        parametersOf(this)
    }
    private val commonViewModel by viewModel<CommonViewModel>()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNav = findViewById(R.id.bottom_nav_view)

        val onLayoutChangeListener: OnLayoutChangeListener = object : OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                commonViewModel.bottomNavHeight = v.height
                v.removeOnLayoutChangeListener(this)
            }
        }

        bottomNav.addOnLayoutChangeListener(onLayoutChangeListener)

        bottomNav.setupWithNavController(navController)
        bottomNav.menu.findItem(R.id.animals_feed_graph).isChecked = true

        navController.addOnDestinationChangedListener { _, destination, _ ->
            run {
                when (destination.id) {
                    R.id.addKennelFragment -> showBottomNav()
                    R.id.userProfileFragment -> showBottomNav()
                    R.id.animalsListFragment -> {
                        showBottomNav()
                        bottomNav.menu.findItem(R.id.animals_feed_graph).isChecked = true

                    }
                    else -> hideBottomNav()
                }
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_graph -> {
                    val token: String = userPropertiesRepository.getUserToken()
                    if (token == "") {
                        bottomSheetDialogShower.createAndShowBottomSheetDialog()
                        return@setOnItemSelectedListener false
                    } else {
                        when (navController.currentBackStackEntry?.destination?.label) {

                            ANIMAL_LIST_LABEL -> {
                                val currentFragment =
                                    supportFragmentManager.currentNavigationFragment
                                currentFragment?.exitTransition =
                                    MaterialSharedAxis(MaterialSharedAxis.X, true)
                                navigator.moveFromAnimalListToUserProfile()
                            }
                            ADD_KENNEL_LABEL -> navigator.moveFromAddKennelToUserProfile()
                        }
                    }
                    true
                }
                R.id.kennel_graph -> {
                    val token: String = userPropertiesRepository.getUserToken()
                    if (token == "") {
                        bottomSheetDialogShower.createAndShowBottomSheetDialog()
                        return@setOnItemSelectedListener false
                    } else {
                        when (navController.currentBackStackEntry?.destination?.label) {
                            ANIMAL_LIST_LABEL -> {
                                navigator.moveFromAnimalListToAddKennel(
                                    FragmentsListForAssigningAnimation.ANIMAL_LIST
                                )
                            }
                            USER_PROFILE_LABEL -> navigator.moveFromUserProfileToAddKennel()
                        }
                    }
                    true
                }
                R.id.animals_feed_graph -> {
                    when (navController.currentBackStackEntry?.destination?.label) {
                        ADD_KENNEL_LABEL -> {
                            navigator.moveFromAddKennelToAnimalList(
                                FragmentsListForAssigningAnimation.ADD_KENNEL
                            )
                        }
                        USER_PROFILE_LABEL -> {
                            navigator.moveFromUserProfileToAnimalList(
                                FragmentsListForAssigningAnimation.USER_PROFILE
                            )
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun hideBottomNav() {
        bottomNav.visibility = View.GONE
    }

    private fun showBottomNav() {
        bottomNav.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        navigator.bind(navController = navController)
    }

    override fun onPause() {
        super.onPause()
        navigator.unbind()
    }
}

val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()





