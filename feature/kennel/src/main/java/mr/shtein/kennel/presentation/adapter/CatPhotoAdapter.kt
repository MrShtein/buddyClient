package mr.shtein.kennel.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.data.model.Animal
import mr.shtein.kennel.R
import mr.shtein.network.ImageLoader

class CatPhotoAdapter(
    private val animalsList: List<Animal>,
    val token: String,
    private val animalTouchCallback: OnAnimalItemClickListener,
    private val networkImageLoader: ImageLoader
) : RecyclerView.Adapter<CatPhotoAdapter.AnimalInKennelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalInKennelViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return AnimalInKennelViewHolder(
            inflater.inflate(R.layout.animal_in_kennel_row, parent, false),
            animalTouchCallback
        )
    }

    override fun onBindViewHolder(holder: AnimalInKennelViewHolder, position: Int) {
        val animalCard = getItem(position)
        holder.bind(animalCard)
    }

    override fun getItemCount(): Int {
        return animalsList.size
    }

    private fun getItem(position: Int): Animal = animalsList[position]


    inner class AnimalInKennelViewHolder(
        private val itemView: View,
        private val onItemListener: OnAnimalItemClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val avatar = itemView.findViewById<ImageButton>(R.id.animal_in_kennel_avatar)

        init {
            avatar.setOnClickListener(this)
        }

        fun bind(animalCard: Animal) {
            val animalAvatarUrl = animalCard.imgUrl.find {
                it.primary
            }
            val endpoint = itemView.resources.getString(R.string.animal_photo_endpoint)
            val catPlaceholder = itemView.context.getDrawable(R.drawable.light_dog_placeholder)
            networkImageLoader.setPhotoToView(
                avatar,
                endpoint,
                animalAvatarUrl?.url!!,
                catPlaceholder!!
            )
        }


        override fun onClick(p0: View?) {
            onItemListener.onClick(animalsList[absoluteAdapterPosition])
        }
    }

    interface OnAnimalItemClickListener {
        fun onClick(animalItem: Animal)
    }
}