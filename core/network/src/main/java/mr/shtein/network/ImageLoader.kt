package mr.shtein.network

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.google.android.material.imageview.ShapeableImageView

interface ImageLoader {
    fun setPhotoToView(
        imageView: ImageView,
        endpoint: String,
        path: String,
        placeHolder: Drawable?
    )
    fun setPhotoToView(
        imageView: ShapeableImageView,
        personAvatarUri: String,
        placeholder: Drawable?
    )
}