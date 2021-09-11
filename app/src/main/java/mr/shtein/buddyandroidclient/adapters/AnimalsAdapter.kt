package mr.shtein.buddyandroidclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mr.shtein.buddyandroidclient.AnimalsViewHolder
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.model.Animal

class AnimalsAdapter(
    context: Context,
    var animals: List<Animal>
) : RecyclerView.Adapter<AnimalsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalsViewHolder {
        return AnimalsViewHolder(inflater.inflate(R.layout.animal_row, parent, false))
    }

    override fun onBindViewHolder(holder: AnimalsViewHolder, position: Int) {
        Picasso.get().load("http://10.0.2.2:8881/static/01.png").into(holder.itemView.findViewById<ImageView>(R.id.animal_image))
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return animals.size
    }

    private fun getItem(position: Int): Animal = animals[position]
}