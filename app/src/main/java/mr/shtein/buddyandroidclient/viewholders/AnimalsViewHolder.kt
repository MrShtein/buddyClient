package mr.shtein.buddyandroidclient.viewholders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import mr.shtein.buddyandroidclient.ProtoAnimalsViewHolder
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.OnAnimalCardClickListener
import mr.shtein.buddyandroidclient.model.Animal

class AnimalsViewHolder(itemView: View, var onAnimalCardClickListener: OnAnimalCardClickListener) : ProtoAnimalsViewHolder(itemView), View.OnClickListener {

    private val animalName: TextView = itemView.findViewById(R.id.animal_row_name)
    private val gender: TextView = itemView.findViewById(R.id.gender_name)
    private val age: TextView = itemView.findViewById(R.id.animal_row_approximately_age)
    private val breed: TextView = itemView.findViewById(R.id.animal_row_breed_name)
    private val animalColor: TextView = itemView.findViewById(R.id.animal_row_color)
    private var animalId: Long = 0

    init {
        itemView.setOnClickListener(this)
    }


    fun bind(animal: Animal) {
        animalId = animal.id
        val currentContext: Context = animalName.context
        this.animalName.text =  animal.name
        this.gender.text = currentContext.getString(R.string.animal_gender, animal.gender)
        this.age.text = currentContext.getString(R.string.animal_age, makeAgeString(animal.age))
        this.breed.text = currentContext.getString(R.string.animal_breed, animal.breed)
        this.animalColor.text =  currentContext.getString(R.string.animal_color, animal.characteristics["color"])
    }

    override fun onClick(v: View?) {
        onAnimalCardClickListener.onAnimalCardClick(animalId)
    }

    private fun makeAgeString(months: Int): String {
        val ageFromMonths = months / 12
        return if (ageFromMonths == 0) {
            "$months мес."
        } else if(ageFromMonths == 1 || ageFromMonths == 21 || ageFromMonths == 31 || ageFromMonths == 41) {
            "$ageFromMonths год"
        } else if (ageFromMonths in 2..4 || ageFromMonths in 22..24 || ageFromMonths in 32..34 || ageFromMonths in 42..44) {
            "$ageFromMonths года"
        } else {
            "$ageFromMonths лет"
        }
    }
}