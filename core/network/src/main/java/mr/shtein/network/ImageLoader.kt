package mr.shtein.network

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.google.android.material.imageview.ShapeableImageView

interface ImageLoader {
    fun setPhotoToView(
        imageView: ImageView,
        endPoint: String,
        path: String,
        placeHolder: Drawable?
    )
    fun setPhotoToView(
        imageView: ShapeableImageView,
        endPoint: String,
        placeholder: Drawable?,
        token: String,
        path: String
    )
}