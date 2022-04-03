package mr.shtein.buddyandroidclient.model

import android.net.Uri
import android.widget.ImageButton
import android.widget.ImageView

data class ImageContainer(
    val id: Int,
    var uri: Uri?,
    val imageView: ImageView,
    val addBtn: ImageButton,
    val cancelBtn: ImageButton
)
