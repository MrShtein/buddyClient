package mr.shtein.buddyandroidclient.utils

import androidx.recyclerview.widget.DiffUtil
import mr.shtein.data.model.Animal
import mr.shtein.data.model.LocationState

class AnimalDiffUtil(
    var oldList: List<mr.shtein.data.model.Animal>,
    var newList: List<mr.shtein.data.model.Animal>) : DiffUtil.Callback() {

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
        if (oldItem.distance != newItem.distance) return mr.shtein.data.model.LocationState.DISTANCE_VISIBLE_STATE
        if (newItem.locationState == mr.shtein.data.model.LocationState.INIT_STATE) return mr.shtein.data.model.LocationState.INIT_STATE
        if (newItem.locationState == mr.shtein.data.model.LocationState.SEARCH_STATE) return mr.shtein.data.model.LocationState.SEARCH_STATE
        if (newItem.locationState == mr.shtein.data.model.LocationState.BAD_RESULT_STATE) return mr.shtein.data.model.LocationState.BAD_RESULT_STATE
        return null
    }
}