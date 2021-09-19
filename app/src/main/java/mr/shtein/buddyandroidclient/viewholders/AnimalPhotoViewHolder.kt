package mr.shtein.buddyandroidclient.viewholders

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import jp.wasabeef.picasso.transformations.CropTransformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import mr.shtein.buddyandroidclient.R

class AnimalPhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val animalImage: ImageView = itemView.findViewById(R.id.big_animal_image)

    fun bind(url: String) {
        val path: String = "http://10.0.2.2:8881/animalsPhoto/$url"

        Picasso
            .get()
            .load(path)
            .transform(RoundedCornersTransformation(20,  0, RoundedCornersTransformation.CornerType.ALL))
            .into(animalImage)
    }

}