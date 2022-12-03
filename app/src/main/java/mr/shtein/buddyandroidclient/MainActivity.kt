package mr.shtein.buddyandroidclient

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.buddyandroidclient.navigator.Navigator
import mr.shtein.buddyandroidclient.utils.BottomSheetDialogShower
import mr.shtein.ui_util.FragmentsListForAssigningAnimation
import mr.shtein.data.repository.UserPropertiesRepository
import org.koin.android.ext.android.inject

private const val USER_PROFILE_LABEL = "UserProfileFragment"
private const val ANIMAL_LIST_LABEL = "animals_list_fragment"
private const val ADD_KENNEL_LABEL = "AddKennelFragment"
private const val LAST_FRAGMENT_KEY = "last_fragment"

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val navigator: Navigator by inject()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNav = findViewById(R.id.bottom_nav_view)


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
            when(item.itemId) {
                R.id.profile_graph -> {
                    val token: String = userPropertiesRepository.getUserToken()
                    if (token == "") {
                        BottomSheetDialogShower.createAndShowBottomSheetDialog(bottomNav, navController)
                        return@setOnItemSelectedListener false
                    } else {
                        when (navController.currentBackStackEntry?.destination?.label) {

                            ANIMAL_LIST_LABEL -> {
                                val currentFragment = supportFragmentManager.currentNavigationFragment
                                currentFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                                navController.navigate(
                                    R.id.action_animalsListFragment_to_userProfileFragment
                                )
                            }
                            ADD_KENNEL_LABEL -> navController.navigate(
                                R.id.action_addKennelFragment_to_userProfileFragment
                            )
                        }
                    }
                    true
                }
                R.id.kennel_graph -> {
                    val token: String = userPropertiesRepository.getUserToken()
                    if (token == "") {
                        BottomSheetDialogShower.createAndShowBottomSheetDialog(bottomNav, navController)
                        return@setOnItemSelectedListener false
                    } else {
                        when (navController.currentBackStackEntry?.destination?.label) {
                            ANIMAL_LIST_LABEL -> {
                                val lastFragmentBundle = bundleOf()
                                lastFragmentBundle.putParcelable(LAST_FRAGMENT_KEY, FragmentsListForAssigningAnimation.ANIMAL_LIST)
                                navController.navigate(
                                    R.id.action_animalsListFragment_to_addKennelFragment, lastFragmentBundle
                                )
                            }
                            USER_PROFILE_LABEL  -> navController.navigate(
                                R.id.action_userProfileFragment_to_addKennelFragment
                            )
                        }
                    }
                    true
                }
                R.id.animals_feed_graph -> {
                    when (navController.currentBackStackEntry?.destination?.label) {
                        ADD_KENNEL_LABEL -> {
                            val lastFragmentBundle = bundleOf()
                            lastFragmentBundle.putParcelable(LAST_FRAGMENT_KEY, FragmentsListForAssigningAnimation.ADD_KENNEL)
                            navController.navigate(
                                R.id.action_addKennelFragment_to_animalsListFragment, lastFragmentBundle
                            )
                        }
                        USER_PROFILE_LABEL  -> {
                            val lastFragmentBundle = bundleOf()
                            lastFragmentBundle.putParcelable(LAST_FRAGMENT_KEY, FragmentsListForAssigningAnimation.USER_PROFILE)
                            navController.navigate(
                                R.id.action_userProfileFragment_to_animalsListFragment, lastFragmentBundle
                            )
                        }
                    }
                    true
                } else -> false
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

fun Fragment.changeMarginBottom(view: View, mainActivity: MainActivity) {
    val bottomNavHeight = mainActivity.bottomNav.height
    val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(0, 0, 0, bottomNavHeight)
    view.layoutParams = layoutParams
}

val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()



