package mr.shtein.buddyandroidclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.buddyandroidclient.presentation.screen.OnLocationBtnClickListener
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.LocationState
import mr.shtein.buddyandroidclient.utils.AnimalDiffUtil
import mr.shtein.buddyandroidclient.viewholders.AnimalsViewHolder

class AnimalsAdapter(
    context: Context,
    var animals: List<Animal>,
    var onAnimalCardClickListener: OnAnimalCardClickListener,
    var onLocationBtnClickListener: OnLocationBtnClickListener
) : RecyclerView.Adapter<AnimalsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalsViewHolder {
        return AnimalsViewHolder(
            inflater.inflate(R.layout.animal_row, parent, false),
            onAnimalCardClickListener,
            onLocationBtnClickListener
        )
    }

    override fun onBindViewHolder(holder: AnimalsViewHolder, position: Int) {
        holder.bind(animals[position])

    }

    override fun onBindViewHolder(
        holder: AnimalsViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when {
            payloads.isEmpty() -> {
                super.onBindViewHolder(holder, position, payloads)
            }
            payloads[0] is LocationState -> {
                holder.makeViewByState(payloads[0] as LocationState, animals[position].distance)
            }
            else -> {
                holder.bindDistanceText(payloads[0].toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return animals.size
    }

    fun updateAnimalList(newAnimalList: List<Animal>) {
        val animalDiffUtil = AnimalDiffUtil(animals, newAnimalList)
        val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
        this.animals = newAnimalList.toList()
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getItem(position: Int): Animal = animals[position]

}