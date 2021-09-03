package mr.shtein.buddyandroidclient

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.model.AnimalChoiceItem

class AnimalsViewHolder(var view: View): RecyclerView.ViewHolder(view) {
    private val animal: TextView = itemView.findViewById(R.id.animal_name)

    fun bind(animalChoiceItem: AnimalChoiceItem) {
        this.animal.text = animalChoiceItem.animalName
    }
}