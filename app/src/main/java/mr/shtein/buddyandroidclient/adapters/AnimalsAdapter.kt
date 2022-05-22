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
import mr.shtein.buddyandroidclient.viewholders.AnimalsViewHolder

class AnimalsAdapter(
    context: Context,
    var animals: List<Animal>,
    var onAnimalCardClickListener: OnAnimalCardClickListener
) : RecyclerView.Adapter<AnimalsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalsViewHolder {
        return AnimalsViewHolder(inflater.inflate(R.layout.animal_row, parent, false), onAnimalCardClickListener)
    }

    override fun onBindViewHolder(holder: AnimalsViewHolder, position: Int) {
                val primaryUrl: List<AnimalPhoto> = getItem(position).imgUrl.filter { it.primary }
                val url: String = primaryUrl[0].url
                val path: String = "http://10.0.2.2:8881/api/v1/animal/photo/$url"
                Log.d("mag", path)
                Picasso.get()
                    .load(path)
                    .into(holder.itemView.findViewById<ImageView>(R.id.animal_row_image))
                holder.bind(animals[position])

    }

    override fun getItemCount(): Int {
        return animals.size
    }

    private fun getItem(position: Int): Animal = animals[position]

}