package mr.shtein.kennel.presentation.adapter

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.model.volunteer.VolunteerDTO
import mr.shtein.model.volunteer.VolunteersBid
import mr.shtein.network.ImageLoader

class VolunteerAndBidAdapter(
    private val onVolunteerItemClick: OnVolunteerItemClickListener,
    private val onBidBtnClick: OnBidBtnClickListener,
    private val imageLoader: ImageLoader,
    private val userPropertiesRepository: UserPropertiesRepository
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: MutableList<VolunteerAndBidItems> = mutableListOf()

    fun updateItems(items: MutableList<VolunteerAndBidItems>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.bid_header_item -> BidHeaderViewHolder(view)
            R.layout.bid_item -> BidItemViewHolder(view)
            R.layout.volunteer_header_item -> VolunteerHeaderViewHolder(view)
            else -> {
                VolunteerItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is BidHeaderViewHolder -> holder.onBindView(item as VolunteerAndBidItems.BidHeader)
            is BidItemViewHolder -> holder.onBindView(item as VolunteerAndBidItems.BidBody)
            is VolunteerHeaderViewHolder -> holder.onBindView(
                item as VolunteerAndBidItems.VolunteerHeader
            )
            is VolunteerItemViewHolder -> holder.onBindView(
                item as VolunteerAndBidItems.VolunteerBody
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is VolunteerAndBidItems.BidHeader -> R.layout.bid_header_item
            is VolunteerAndBidItems.BidBody -> R.layout.bid_item
            is VolunteerAndBidItems.VolunteerHeader -> R.layout.volunteer_header_item
            is VolunteerAndBidItems.VolunteerBody -> R.layout.volunteer_item
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class BidHeaderViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val bidsItemText: TextView = itemView.findViewById(R.id.bid_header_item_amount)
        fun onBindView(bidsAmountContainer: VolunteerAndBidItems.BidHeader) {
            val bidsAmountStr: String = itemView.context.resources.getQuantityString(
                R.plurals.bid_found_count,
                bidsAmountContainer.bidsCount,
                bidsAmountContainer.bidsCount
            )
            val bidsAmountColorized: SpannableString = SpannableString(bidsAmountStr)
            val numberRegex = "\\d+".toRegex()
            val match = numberRegex.find(bidsAmountStr)
            val matchRange: IntRange = match!!.range
            val redColor = itemView.resources.getColor(R.color.error50, null)
            bidsAmountColorized.setSpan(
                ForegroundColorSpan(redColor),
                0,
                matchRange.last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            bidsItemText.text = bidsAmountColorized
        }
    }

    inner class BidItemViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        fun onBindView(bidsContainer: VolunteerAndBidItems.BidBody) {
            with(itemView.findViewById<MaterialButton>(R.id.bid_item_confirm_btn)) {
                setOnClickListener {
                    onBidBtnClick.onConfirmClick(bidsContainer.bidBody)
                }
            }
            with(itemView.findViewById<MaterialButton>(R.id.bid_item_reject_btn)) {
                setOnClickListener {
                    onBidBtnClick.onRejectClick(bidsContainer.bidBody)
                }
            }
            with(itemView.findViewById<ShapeableImageView>(R.id.bid_item_avatar)) {
                val token = userPropertiesRepository.getUserToken()
                val endpoint = itemView.resources.getString(R.string.user_avatar_endpoint)
                val avatarUrl: String? = bidsContainer.bidBody.avatarUrl
                avatarUrl?.let {
                    imageLoader.setPhotoToView(
                        imageView = this,
                        endPoint = endpoint,
                        placeholder = getPersonPlaceholder(itemView = itemView),
                        token = token,
                        path = it
                    )
                }
            }
            with(itemView.findViewById<TextView>(R.id.bid_item_user_name)) {
                this.text = bidsContainer.bidBody.name
            }
        }
    }

    inner class VolunteerHeaderViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun onBindView(volunteersAmountContainer: VolunteerAndBidItems.VolunteerHeader) {
            with(itemView.findViewById<TextView>(R.id.volunteer_header_item_count)) {
                this.text = itemView.context.resources.getQuantityString(
                    R.plurals.volunteers_found_count,
                    volunteersAmountContainer.volunteersCount,
                    volunteersAmountContainer.volunteersCount
                )
            }
        }
    }

    inner class VolunteerItemViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun onBindView(volunteerItemContainer: VolunteerAndBidItems.VolunteerBody) {
            itemView.setOnClickListener {
                onVolunteerItemClick.onVolunteerCardClick(volunteerItemContainer.volunteerBody)
            }
            with(itemView.findViewById<ShapeableImageView>(R.id.volunteer_item_avatar)) {
                val avatarUrl = volunteerItemContainer.volunteerBody.avatarUrl
                val token = userPropertiesRepository.getUserToken()
                val endpoint = itemView.resources.getString(R.string.user_avatar_endpoint)
                avatarUrl?.let {
                    imageLoader.setPhotoToView(
                        imageView = this,
                        endPoint = endpoint,
                        placeholder = getPersonPlaceholder(itemView = itemView),
                        token = token,
                        path = it
                    )
                }
            }
            with(itemView.findViewById<TextView>(R.id.volunteer_item_user_name)) {
                this.text = volunteerItemContainer.volunteerBody.name
            }
        }
    }

    private fun getPersonPlaceholder(itemView: View): Drawable? {
        return AppCompatResources.getDrawable(
            itemView.context,
            R.drawable.user_photo_placeholder
        )
    }

    interface OnBidBtnClickListener {
        fun onConfirmClick(bidItem: VolunteersBid)
        fun onRejectClick(bidItem: VolunteersBid)
    }

    interface OnVolunteerItemClickListener {
        fun onVolunteerCardClick(volunteer: VolunteerDTO)
    }
}

