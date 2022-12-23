package mr.shtein.kennel.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import mr.shtein.data.model.KennelPreview
import mr.shtein.kennel.R
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.dp
import mr.shtein.ui_util.px

class KennelsAdapter(
    var kennels: List<KennelPreview>,
    var kennelTouchCallback: OnKennelItemClickListener,
    private val networkImageLoader: ImageLoader
) : RecyclerView.Adapter<KennelsAdapter.KennelsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KennelsViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return KennelsViewHolder(
            inflater.inflate(R.layout.kennel_row, parent, false),
            kennelTouchCallback
        )
    }

    override fun onBindViewHolder(kennelViewHolder: KennelsViewHolder, position: Int) {
        val kennelCard = getItem(position)
        kennelViewHolder.bind(kennelCard, position)
    }

    override fun getItemCount(): Int {
        return kennels.size
    }

    private fun getItem(position: Int): KennelPreview = kennels[position]

    fun setNewData(kennelPreviewList: List<KennelPreview>) {
        kennels = kennelPreviewList
        notifyDataSetChanged()
    }

    inner class KennelsViewHolder(
        private val itemView: View,
        private val onKennelListener: OnKennelItemClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val avatar: ShapeableImageView = itemView.findViewById(R.id.kennel_row_image)
        private val kennelName: TextView = itemView.findViewById(R.id.kennel_row_name)
        private val volunteers: TextView = itemView.findViewById(R.id.kennel_row_volunteers_amount)
        private val animalsAmount: TextView = itemView.findViewById(R.id.kennel_row_animals_amount)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(kennelPreviewItem: KennelPreview, position: Int) {
            if (position == itemCount - 1) addMarginBottomToFinalElement()
            val endpoint = itemView.resources.getString(R.string.kennel_avatar_endpoint)
            val photoName = kennelPreviewItem.avatarUrl
            val dogPlaceholder = itemView.context.getDrawable(R.drawable.light_dog_placeholder)
            networkImageLoader.setPhotoToView(
                avatar,
                endpoint,
                photoName,
                dogPlaceholder!!
            )

            kennelName.text = kennelPreviewItem.name
            volunteers.text = makeVolunteersText(kennelPreviewItem.volunteersAmount)
            animalsAmount.text = itemView.resources.getQuantityString(
                R.plurals.buddy_found_count_without_word_find,
                kennelPreviewItem.animalsAmount,
                kennelPreviewItem.animalsAmount
            )
        }

        override fun onClick(v: View?) {
            onKennelListener.onClick(kennels[absoluteAdapterPosition])
        }

        private fun addMarginBottomToFinalElement() {
            val params = itemView.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(
                params.marginStart,
                params.topMargin,
                params.marginEnd,
                params.bottomMargin + 40.px)
            itemView.layoutParams = params
        }
    }

    interface OnKennelItemClickListener {
        fun onClick(kennelItem: KennelPreview)
    }



    private fun makeVolunteersText(amount: Int): String {
        val mainText = "волонтер"
        return when(amount) {
            1 -> "1 $mainText"
            2, 3, 4, -> "$amount ${mainText}а"
            else -> "$amount ${mainText}ов"
        }
    }
}