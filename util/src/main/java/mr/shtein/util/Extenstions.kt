package mr.shtein.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar

fun Fragment.showMessage(message: String) {
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

fun View.changeMargin(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null,
) {
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(left ?: 0, top ?: 0, right ?: 0, bottom ?: 0)
    this.layoutParams = layoutParams
}

fun View.changePadding(
    left: Int = this.paddingLeft,
    top: Int = this.paddingTop,
    right: Int = this.paddingRight,
    bottom: Int = this.paddingBottom,
) {
    this.setPadding(left, right, right, bottom)
}