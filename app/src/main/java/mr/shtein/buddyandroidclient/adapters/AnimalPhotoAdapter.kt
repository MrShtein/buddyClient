package mr.shtein.buddyandroidclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import mr.shtein.buddyandroidclient.BuildConfig
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.utils.ImageLoader

class AnimalPhotoAdapter(
    var animalPhotos: List<String>,
) : RecyclerView.Adapter<AnimalPhotoAdapter.AnimalPhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalPhotoViewHolder {
        val context: Context = parent.context
        return AnimalPhotoViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.animal_big_photo_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AnimalPhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun getItem(position: Int): String {
        return animalPhotos[position]
    }

    override fun getItemCount(): Int {
        return animalPhotos.size
    }

    inner class AnimalPhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)    {

        private val animalImage: ImageView = itemView.findViewById(R.id.animal_big_photo_row_img)

        fun bind(url: String) {
            val host = BuildConfig.HOST
            val endpoint = itemView.context.resources.getString(R.string.animal_photo_endpoint)
            val imageLoader = ImageLoader(host, endpoint, url)
            imageLoader.setPhotoToView(animalImage)
        }
    }
}
