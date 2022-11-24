package mr.shtein.buddyandroidclient.utils

import androidx.recyclerview.widget.DiffUtil
import mr.shtein.data.model.KennelPreview

class KennelDiffUtil(var oldList: List<mr.shtein.data.model.KennelPreview>, var newList: List<mr.shtein.data.model.KennelPreview>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.kennelId == newItem.kennelId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.animalsAmount == newItem.animalsAmount &&
                oldItem.volunteersAmount == newItem.volunteersAmount
    }
}