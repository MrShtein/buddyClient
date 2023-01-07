package mr.shtein.network

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView


class NetworkImageLoader(
    private val host: String,
) : ImageLoader {
    override fun setPhotoToView(
        imageView: ImageView,
        endpoint: String,
        path: String,
        placeHolder: Drawable?
    ) {
        val fullUrl = "${host}${endpoint}${path}"
        val cropOptions = RequestOptions().centerCrop()
        Glide.with(imageView.context)
            .load(fullUrl)
            .apply(cropOptions)
            .placeholder(placeHolder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
    override fun setPhotoToView(
        imageView: ShapeableImageView,
        personAvatarUri: String,
        placeholder: Drawable?
    ) {
        val cropOptions = RequestOptions().centerCrop()
        Glide.with(imageView.context)
            .load(personAvatarUri)
            .apply(cropOptions)
            .placeholder(placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}
