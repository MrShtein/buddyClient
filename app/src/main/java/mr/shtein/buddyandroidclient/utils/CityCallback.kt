package mr.shtein.buddyandroidclient.utils

import androidx.recyclerview.widget.DiffUtil
import mr.shtein.buddyandroidclient.model.CityChoiceItem

class CityCallback(
    private val oldList: List<CityChoiceItem>,
    private val newList: List<CityChoiceItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].cityName == newList[newItemPosition].cityName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}