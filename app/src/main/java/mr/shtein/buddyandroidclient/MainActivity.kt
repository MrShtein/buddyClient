package mr.shtein.buddyandroidclient

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mr.shtein.buddyandroidclient.utils.BottomSheetDialogShower
import mr.shtein.buddyandroidclient.utils.SharedPreferences

private const val USER_PROFILE_LABEL = "UserProfileFragment"
private const val ANIMAL_LIST_LABEL = "animals_list_fragment"
private const val ADD_KENNEL_LABEL = "AddKennelFragment"


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

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in)
            .setExitAnim(R.anim.slide_out)
            .setPopUpTo(R.id.startFragment, true)
            .build()

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
                    ANIMAL_LIST_LABEL -> navController.navigate(
                        R.id.action_animalsListFragment_to_userProfileFragment, null, navOptions
                    )
                    ADD_KENNEL_LABEL -> navController.navigate(
                        R.id.action_addKennelFragment_to_userProfileFragment, null, navOptions
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
                    ANIMAL_LIST_LABEL -> navController.navigate(
                        R.id.action_animalsListFragment_to_addKennelFragment, null, navOptions
                    )
                    USER_PROFILE_LABEL  -> navController.navigate(
                        R.id.action_userProfileFragment_to_addKennelFragment, null, navOptions
                    )
                }
            }
            true
        }

        bottomNav.menu.findItem(R.id.animals_feed_graph).setOnMenuItemClickListener {
            when (navController.currentBackStackEntry?.destination?.label) {
                ADD_KENNEL_LABEL -> navController.navigate(
                    R.id.action_addKennelFragment_to_animalsListFragment, null, navOptions
                )
                USER_PROFILE_LABEL  -> navController.navigate(
                    R.id.action_userProfileFragment_to_animalsListFragment, null, navOptions
                )
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



