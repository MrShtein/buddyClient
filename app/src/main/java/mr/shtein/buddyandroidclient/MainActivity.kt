package mr.shtein.buddyandroidclient

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mr.shtein.buddyandroidclient.utils.SharedPreferences

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
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



