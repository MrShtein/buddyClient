package mr.shtein.network

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.imageview.ShapeableImageView

class NetworkImageLoader(
    private val host: String,
) : ImageLoader {
    override fun setPhotoToView(
        imageView: ImageView,
        endpoint: String,
        path: String,
        placeHolder: Drawable
    ) {
        val fullUrl = "${host}${endpoint}${path}"
        Glide.with(imageView.context)
            .load(fullUrl)
            .placeholder(placeHolder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
    override fun setPhotoToView(
        imageView: ShapeableImageView,
        personAvatarUri: String,
        placeholder: Drawable
    ) {
        Glide.with(imageView.context)
            .load(personAvatarUri)
            .placeholder(placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}
