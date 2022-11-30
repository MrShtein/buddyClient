package mr.shtein.ui_util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

fun Fragment.setStatusBarColor(isBlack: Boolean) {
    val windowInsetsController =
        WindowCompat.getInsetsController(requireActivity().window, requireActivity().window.decorView)
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