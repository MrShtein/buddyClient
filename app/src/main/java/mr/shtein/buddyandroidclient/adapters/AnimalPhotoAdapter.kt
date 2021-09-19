package mr.shtein.buddyandroidclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.viewholders.AnimalPhotoViewHolder

class AnimalPhotoAdapter(
    private val context: Context,
    private var animalPhotos: List<String>,
    ): RecyclerView.Adapter<AnimalPhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalPhotoViewHolder {
        return AnimalPhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.big_animal_photo_card, parent, false))
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

}