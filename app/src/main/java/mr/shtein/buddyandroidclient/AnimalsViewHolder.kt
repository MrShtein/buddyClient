package mr.shtein.buddyandroidclient

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mr.shtein.buddyandroidclient.model.Animal

class AnimalsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val animalImage: ImageView = itemView.findViewById(R.id.animal_image)
    private val animalName: TextView = itemView.findViewById(R.id.who_is)
    private val kennelName: TextView = itemView.findViewById(R.id.kennel_name)
    private val gender: TextView = itemView.findViewById(R.id.gender_name)
    private val age: TextView = itemView.findViewById(R.id.approximately_age)
    private val breed: TextView = itemView.findViewById(R.id.breed_name)
    private val animalColor: TextView = itemView.findViewById(R.id.animal_color)


    fun bind(animal: Animal) {
        this.animalName.text = animal.name
        this.kennelName.text = animal.kennelName
        this.gender.text = animal.gender
        this.age.text = animal.age.toString()
        this.breed.text = animal.breed
        this.animalColor.text = animal.characteristics["color"]
    }
}