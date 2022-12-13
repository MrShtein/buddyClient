package mr.shtein.ui_util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.network.ImageLoader

class AnimalPhotoAdapter(
    var animalPhotos: List<String>,
    val networkImageLoader: ImageLoader
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

    inner class AnimalPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val animalImage: ImageView = itemView.findViewById(R.id.animal_big_photo_row_img)

        fun bind(url: String) {
            val endpoint = itemView.context.resources.getString(R.string.animal_photo_endpoint)
            val dogPlaceholder = itemView.context.getDrawable(R.drawable.light_dog_placeholder)!!
            networkImageLoader.setPhotoToView(
                animalImage,
                endpoint,
                url,
                dogPlaceholder
            )
        }
    }
}
