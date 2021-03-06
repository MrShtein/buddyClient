package mr.shtein.buddyandroidclient.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.model.KennelPreview
import mr.shtein.buddyandroidclient.utils.ImageLoader

class KennelsAdapter(
    private val token: String,
    var kennels: List<KennelPreview>,
    var kennelTouchCallback: OnKennelItemClickListener
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

    private fun getItem(position: Int): KennelPreview = kennels[position]

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

        fun bind(kennelPreviewItem: KennelPreview) {
            val host = itemView.resources.getString(R.string.host)
            val endpoint = itemView.resources.getString(R.string.kennel_avatar_endpoint)
            val photoName = kennelPreviewItem.avatarUrl
            val imageLoader = ImageLoader(host, endpoint, photoName)
            imageLoader.setPhotoToView(avatar,token)

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
        fun onClick(kennelItem: KennelPreview)
    }

    private fun makeVolunteersText(amount: Int): String {
        val mainText = "????????????????"
        return when(amount) {
            1 -> "1 $mainText"
            2, 3, 4, -> "$amount ${mainText}??"
            else -> "$amount ${mainText}????"
        }
    }
}