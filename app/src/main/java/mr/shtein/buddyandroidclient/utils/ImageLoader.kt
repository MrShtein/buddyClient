package mr.shtein.buddyandroidclient.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

class ImageLoader(
    private val host: String,
    private val endpoint: String,
    private val photoName: String,
) {
    public fun setPhotoToView(imageView: ImageView, token: String = "", placeHolder: Drawable? = null) {
        val fullUrl = "${host}${endpoint}${photoName}"

        if (token.isNotEmpty()) {
            val header = LazyHeaders.Builder()
                .addHeader("Authorization", token)
                .build()
            val headerWithUrl = GlideUrl(
                fullUrl,
                header
            )
            Glide.with(imageView.context)
                .load(headerWithUrl)
                .placeholder(placeHolder)
                .into(imageView)
        } else {
            Glide.with(imageView.context)
                .load(fullUrl)
                .placeholder(placeHolder)
                .into(imageView)
        }
    }
}
