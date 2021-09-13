package mr.shtein.buddyandroidclient

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import mr.shtein.buddyandroidclient.model.Animal

class StatementViewHolder(itemView: View) : ProtoAnimalsViewHolder(itemView) {

    private val statement: TextView = itemView.findViewById(R.id.animal_find_count)

    fun bind(count: Int) {
        this.statement.text = "Найдено: $count объявлений"
    }
}