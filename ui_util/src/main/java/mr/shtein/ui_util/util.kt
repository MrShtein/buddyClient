package mr.shtein.ui_util

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mr.shtein.data.repository.UserPropertiesRepository

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

fun Fragment.showBadTokenDialog(userPropertiesRepository: UserPropertiesRepository) {
    val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)

        .setView(R.layout.bad_token_dialog)
        .setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
        .show()

    val okBtn: Button? = dialog.findViewById(R.id.bad_token_dialog_ok_btn)

    okBtn?.setOnClickListener {
        userPropertiesRepository.saveUserToken("")
    }
}