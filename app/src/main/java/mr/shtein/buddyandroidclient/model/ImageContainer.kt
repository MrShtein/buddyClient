package mr.shtein.buddyandroidclient.model

import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar

data class ImageContainer(
    val id: Int,
    var uri: Uri?,
    var url: String? = null,
    val imageView: ImageView,
    val addBtn: ImageButton,
    val cancelBtn: ImageButton,
    val progressBar: ProgressBar,
    val overlay: View
)
