package mr.shtein.animal.presentation.util

import androidx.recyclerview.widget.DiffUtil
import mr.shtein.data.model.Animal
import mr.shtein.data.model.LocationState

class AnimalDiffUtil(
    var oldList: List<Animal>,
    var newList: List<Animal>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.distance == newItem.distance && oldItem.locationState == newItem.locationState
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem.distance != newItem.distance) return LocationState.DISTANCE_VISIBLE_STATE
        if (newItem.locationState == LocationState.INIT_STATE) return LocationState.INIT_STATE
        if (newItem.locationState == LocationState.SEARCH_STATE) return LocationState.SEARCH_STATE
        if (newItem.locationState == LocationState.BAD_RESULT_STATE) return LocationState.BAD_RESULT_STATE
        return null
    }
}