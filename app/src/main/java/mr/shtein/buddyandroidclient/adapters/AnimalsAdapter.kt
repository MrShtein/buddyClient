package mr.shtein.buddyandroidclient.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import mr.shtein.buddyandroidclient.viewholders.AnimalsViewHolder
import mr.shtein.buddyandroidclient.ProtoAnimalsViewHolder
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.StatementViewHolder
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.AnimalPhoto

private const val ANIMAL_TYPE = 1
private const val STATEMENT_TYPE = 0

class AnimalsAdapter(
    context: Context,
    private var animals: List<Animal>,
    var onAnimalCardClickListener: OnAnimalCardClickListener
) : RecyclerView.Adapter<ProtoAnimalsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProtoAnimalsViewHolder {
        return when (viewType) {
            ANIMAL_TYPE -> AnimalsViewHolder(inflater.inflate(R.layout.animal_row, parent, false), onAnimalCardClickListener)
            else -> StatementViewHolder(inflater.inflate(R.layout.found_animals_row, parent, false))
        }

    }

    override fun onBindViewHolder(holder: ProtoAnimalsViewHolder, position: Int) {
        when(holder) {
            is AnimalsViewHolder -> {
                val primaryUrl: List<AnimalPhoto> = getItem(position).imgUrl.filter { it.primary }
                val url: String = primaryUrl[0].url
                val path: String = "http://10.0.2.2:8881/api/v1/animal/photo/$url"
                Log.d("mag", path)
                Picasso.get()
                    .load(path)
                    .transform(CropCircleTransformation())
                    .into(holder.itemView.findViewById<ImageView>(R.id.animal_image))
                holder.bind(getItem(position))
            }
            is StatementViewHolder -> holder.bind(animals.size)
        }

    }

    override fun getItemCount(): Int {
        return animals.size + 1
    }

    private fun getItem(position: Int): Animal = animals[position - 1]

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> STATEMENT_TYPE
            else -> ANIMAL_TYPE
        }

    }
}