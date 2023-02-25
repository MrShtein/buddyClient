package mr.shtein.util

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar

fun Fragment.showMessage(message: String)  {
    val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
    snackBar.show()
    snackBar.setAction("Ok") {
        snackBar.dismiss()
    }
}

fun Int.dpToPx(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return this * (metrics.densityDpi / 160)
}