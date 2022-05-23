package mr.shtein.buddyandroidclient.viewholders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import mr.shtein.buddyandroidclient.ProtoAnimalsViewHolder
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.OnAnimalCardClickListener
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.AnimalPhoto
import mr.shtein.buddyandroidclient.utils.ImageLoader

class AnimalsViewHolder(itemView: View, var onAnimalCardClickListener: OnAnimalCardClickListener) :
    ProtoAnimalsViewHolder(itemView), View.OnClickListener {

    private val animalImage: ImageView = itemView.findViewById(R.id.animal_row_image)
    private val animalName: TextView = itemView.findViewById(R.id.animal_row_name)
    private val gender: TextView = itemView.findViewById(R.id.gender_name)
    private val age: TextView = itemView.findViewById(R.id.animal_row_approximately_age)
    private val breed: TextView = itemView.findViewById(R.id.animal_row_breed_name)
    private val animalColor: TextView = itemView.findViewById(R.id.animal_row_color)
    private lateinit var animal: Animal

    init {
        itemView.setOnClickListener(this)
    }


    fun bind(animal: Animal) {
        this.animal = animal
        val currentContext: Context = animalName.context
        this.animalName.text = animal.name
        this.gender.text = currentContext.getString(R.string.animal_gender, animal.gender)
        this.age.text = currentContext.getString(R.string.animal_age, makeAgeString(animal.age))
        this.breed.text = currentContext.getString(R.string.animal_breed, animal.breed)
        this.animalColor.text =
            currentContext.getString(R.string.animal_color, animal.characteristics["color"])

        setPrimaryPhoto(animal)

    }

    override fun onClick(v: View?) {
        onAnimalCardClickListener.onAnimalCardClick(animal)
    }

    private fun makeAgeString(age: Int): String {
        return if (age == 1 || age == 21 || age == 31 || age == 41) {
            "$age год"
        } else if (age in 2..4 || age in 22..24 || age in 32..34 || age in 42..44) {
            "$age года"
        } else {
            "$age лет"
        }
    }

    private fun setPrimaryPhoto(animal: Animal) {
        val primaryUrl = animal.imgUrl.filter { it.primary }
        val url: String = primaryUrl[0].url
        val host = itemView.resources.getString(R.string.host)
        val endpoint = itemView.resources.getString(R.string.animal_photo_endpoint)
        val imageLoader = ImageLoader(host, endpoint, url)
        imageLoader.setPhotoToView(animalImage)
    }
}