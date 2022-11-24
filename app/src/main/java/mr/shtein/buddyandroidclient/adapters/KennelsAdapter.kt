package mr.shtein.buddyandroidclient.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import mr.shtein.buddyandroidclient.R
import mr.shtein.data.model.KennelPreview
import mr.shtein.network.ImageLoader

class KennelsAdapter(
    var kennels: List<mr.shtein.data.model.KennelPreview>,
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
        kennelViewHolder.bind(kennelCard)
    }

    override fun getItemCount(): Int {
        return kennels.size
    }

    private fun getItem(position: Int): mr.shtein.data.model.KennelPreview = kennels[position]

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

        fun bind(kennelPreviewItem: mr.shtein.data.model.KennelPreview) {
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
    }

    interface OnKennelItemClickListener {
        fun onClick(kennelItem: mr.shtein.data.model.KennelPreview)
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