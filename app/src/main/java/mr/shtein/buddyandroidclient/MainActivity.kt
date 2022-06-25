package mr.shtein.buddyandroidclient

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.buddyandroidclient.utils.BottomSheetDialogShower
import mr.shtein.buddyandroidclient.utils.WorkFragment
import mr.shtein.buddyandroidclient.utils.SharedPreferences

private const val USER_PROFILE_LABEL = "UserProfileFragment"
private const val ANIMAL_LIST_LABEL = "animals_list_fragment"
private const val ADD_KENNEL_LABEL = "AddKennelFragment"
private const val LAST_FRAGMENT_KEY = "last_fragment"

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNav = findViewById(R.id.bottom_nav_view)


        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            run {
                when (destination.id) {
                    R.id.addKennelFragment -> showBottomNav()
                    R.id.userProfileFragment -> showBottomNav()
                    R.id.animalsListFragment -> showBottomNav()
                    else -> hideBottomNav()
                }
            }
        }

        bottomNav.menu.findItem(R.id.profile_graph).setOnMenuItemClickListener {
            val sharedPreferenceStore =
                SharedPreferences(this, SharedPreferences.PERSISTENT_STORAGE_NAME)
            val token: String = sharedPreferenceStore.readString(SharedPreferences.TOKEN_KEY, "")
            if (token == "") {
                BottomSheetDialogShower.createAndShowBottomSheetDialog(bottomNav, navController)
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

        bottomNav.menu.findItem(R.id.kennel_graph).setOnMenuItemClickListener {
            val sharedPreferenceStore =
                SharedPreferences(this, SharedPreferences.PERSISTENT_STORAGE_NAME)
            val token: String = sharedPreferenceStore.readString(SharedPreferences.TOKEN_KEY, "")
            if (token == "") {
                BottomSheetDialogShower.createAndShowBottomSheetDialog(bottomNav, navController)
            } else {
                when (navController.currentBackStackEntry?.destination?.label) {
                    ANIMAL_LIST_LABEL -> {
                        val lastFragmentBundle = bundleOf()
                        lastFragmentBundle.putParcelable(LAST_FRAGMENT_KEY, WorkFragment.ANIMAL_LIST)
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

        bottomNav.menu.findItem(R.id.animals_feed_graph).setOnMenuItemClickListener {
            when (navController.currentBackStackEntry?.destination?.label) {
                ADD_KENNEL_LABEL -> {
                    val lastFragmentBundle = bundleOf()
                    lastFragmentBundle.putParcelable(LAST_FRAGMENT_KEY, WorkFragment.ADD_KENNEL)
                    navController.navigate(
                        R.id.action_addKennelFragment_to_animalsListFragment, lastFragmentBundle
                    )
                }
                USER_PROFILE_LABEL  -> {
                    val lastFragmentBundle = bundleOf()
                    lastFragmentBundle.putParcelable(LAST_FRAGMENT_KEY, WorkFragment.USER_PROFILE)
                    navController.navigate(
                        R.id.action_userProfileFragment_to_animalsListFragment, lastFragmentBundle
                    )
                }
            }
            true
        }
    }

    private fun hideBottomNav() {
        bottomNav.visibility = View.GONE
    }

    private fun showBottomNav() {
        bottomNav.visibility = View.VISIBLE
    }
}

fun Fragment.showBadTokenDialog() {
    val storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
    val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)

        .setView(R.layout.bad_token_dialog)
        .setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
        .show()

    val okBtn: Button? = dialog.findViewById(R.id.bad_token_dialog_ok_btn)

    okBtn?.setOnClickListener {
        storage.writeString(SharedPreferences.TOKEN_KEY, "")
    }
}

fun Fragment.setStatusBarColor(isBlack: Boolean) {
    val windowInsetsController =
        ViewCompat.getWindowInsetsController(requireActivity().window.decorView)
    windowInsetsController!!.isAppearanceLightStatusBars = isBlack
}

fun Fragment.setInsetsListenerForPadding(
    view: View,
    left: Boolean,
    top: Boolean,
    right: Boolean,
    bottom: Boolean
) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        var leftInt = 0
        var topInt = 0
        var rightInt = 0
        var bottomInt = 0

        if (left) leftInt = insets.left
        if (top) topInt = insets.top
        if (right) rightInt = insets.right
        if (bottom) bottomInt = insets.bottom

        view.setPadding(leftInt, topInt, rightInt, bottomInt)

        WindowInsetsCompat.CONSUMED
    }
}

val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()



