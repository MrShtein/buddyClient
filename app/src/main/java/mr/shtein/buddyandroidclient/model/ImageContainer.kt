package mr.shtein.buddyandroidclient.model

import android.widget.ImageButton
import android.widget.ImageView

data class ImageContainer(
    val id: Int,
    val imageView: ImageView,
    val addBtn: ImageButton,
    val cancelBtn: ImageButton
)
