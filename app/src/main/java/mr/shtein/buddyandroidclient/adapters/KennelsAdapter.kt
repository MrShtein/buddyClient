package mr.shtein.buddyandroidclient.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.android.material.imageview.ShapeableImageView
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.model.KennelPreview
import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem

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

        private val photoUrl = "http://10.0.2.2:8881/api/v1/kennel/avatar/"
        private val avatar: ShapeableImageView = itemView.findViewById(R.id.kennel_row_image)
        private val kennelName: TextView = itemView.findViewById(R.id.kennel_row_name)
        private val volunteers: TextView = itemView.findViewById(R.id.kennel_row_volunteers_amount)
        private val animalsAmount: TextView = itemView.findViewById(R.id.kennel_row_animals_amount)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(kennelPreviewItem: KennelPreview) {
            val url = "$photoUrl${kennelPreviewItem.avatarUrl}"
            val header = LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            val urlForGlide = GlideUrl(
                url,
                header
            )
            Glide.with(itemView)
                .load(urlForGlide)
                .into(avatar)
            kennelName.text = kennelPreviewItem.name
            volunteers.text = makeVolunteersText(kennelPreviewItem.volunteersAmount)
            animalsAmount.text = itemView.resources.getQuantityString(
                R.plurals.animal_found_count,
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
        val mainText = "волонтер"
        return when(amount) {
            1 -> "1 $mainText"
            2, 3, 4, -> "$amount ${mainText}а"
            else -> "$amount ${mainText}ов"
        }
    }
}