package mr.shtein.network

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.imageview.ShapeableImageView

private const val TAG = "NetworkImageLoader"

class NetworkImageLoader(
    private val host: String,
) : ImageLoader {

    override fun setPhotoToView(
        imageView: ImageView,
        endPoint: String,
        path: String,
        placeHolder: Drawable?
    ) {
        val fullUrl = "${host}${endPoint}${path}"
        val cropOptions = RequestOptions().centerCrop()
        Glide.with(imageView.context)
            .load(fullUrl)
            .apply(cropOptions)
            .apply(RequestOptions.timeoutOf(BuildConfig.NETWORK_TIMEOUT.toInt()))
            .placeholder(placeHolder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
    override fun setPhotoToView(
        imageView: ShapeableImageView,
        endPoint: String,
        placeholder: Drawable?,
        token: String,
        path: String
    ) {
        val cropOptions = RequestOptions().centerCrop()
        val fullUrl = "$host$endPoint/$path"
        val headers = LazyHeaders.Builder()
            .addHeader("Authorization", token)
            .build()
        val glide = GlideUrl(fullUrl, headers)
        Glide.with(imageView.context)
            .load(glide)
            .apply(cropOptions)
            .apply(RequestOptions.timeoutOf(BuildConfig.NETWORK_TIMEOUT.toInt()))
            .placeholder(placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}
