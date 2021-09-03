package mr.shtein.buddyandroidclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.AnimalsViewHolder
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.model.AnimalChoiceItem

class AnimalsAdapter(
    context: Context,
    var animalChoiceItems: List<AnimalChoiceItem>
) : RecyclerView.Adapter<AnimalsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalsViewHolder {
        return AnimalsViewHolder(inflater.inflate(R.layout.animal_row, parent, false))
    }

    override fun onBindViewHolder(holder: AnimalsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return animalChoiceItems.size
    }

    private fun getItem(position: Int): AnimalChoiceItem = animalChoiceItems[position]
}