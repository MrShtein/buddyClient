package mr.shtein.buddyandroidclient.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.AnimalPhoto
import mr.shtein.buddyandroidclient.utils.ImageLoader
import mr.shtein.buddyandroidclient.viewholders.AnimalsViewHolder

class AnimalsAdapter(
    context: Context,
    var animals: List<Animal>,
    var onAnimalCardClickListener: OnAnimalCardClickListener
) : RecyclerView.Adapter<AnimalsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalsViewHolder {
        return AnimalsViewHolder(
            inflater.inflate(R.layout.animal_row, parent, false),
            onAnimalCardClickListener
        )
    }

    override fun onBindViewHolder(holder: AnimalsViewHolder, position: Int) {
        holder.bind(animals[position])

    }

    override fun getItemCount(): Int {
        return animals.size
    }

    private fun getItem(position: Int): Animal = animals[position]

}