package mr.shtein.animal.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import mr.shtein.animal.R
import mr.shtein.animal.presentation.listener.OnAnimalCardClickListener
import mr.shtein.animal.presentation.screen.OnLocationBtnClickListener
import mr.shtein.animal.presentation.util.AnimalDiffUtil
import mr.shtein.data.model.Animal
import mr.shtein.data.model.LocationState
import mr.shtein.network.ImageLoader

class AnimalsAdapter(
    context: Context,
    var animals: List<Animal>,
    var onAnimalCardClickListener: OnAnimalCardClickListener,
    var onLocationBtnClickListener: OnLocationBtnClickListener,
    private val networkImageLoader: ImageLoader
) : RecyclerView.Adapter<AnimalsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalsViewHolder {
        return AnimalsViewHolder(
            inflater.inflate(R.layout.animal_row, parent, false),
            onAnimalCardClickListener,
            onLocationBtnClickListener,
            networkImageLoader
        )
    }

    override fun onBindViewHolder(holder: AnimalsViewHolder, position: Int) {
        holder.bind(animals[position])

    }

    override fun onBindViewHolder(
        holder: AnimalsViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when {
            payloads.isEmpty() -> {
                super.onBindViewHolder(holder, position, payloads)
            }
            payloads[0] is LocationState -> {
                holder.makeViewByState(payloads[0] as LocationState, animals[position].distance)
            }
            else -> {
                holder.bindDistanceText(payloads[0].toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return animals.size
    }

    fun updateAnimalList(newAnimalList: List<Animal>) : Int {
        val previousAnimalListSize = animals.size
        val animalDiffUtil = AnimalDiffUtil(animals, newAnimalList)
        val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
        this.animals = newAnimalList.toList()
        diffResult.dispatchUpdatesTo(this)
        return previousAnimalListSize
    }

    private fun getItem(position: Int): Animal = animals[position]

}


class AnimalsViewHolder(
    private val itemView: View,
    var onAnimalCardClickListener: OnAnimalCardClickListener,
    var onLocationBtnClickListener: OnLocationBtnClickListener,
    private val networkImageLoader: ImageLoader
) : ProtoAnimalsViewHolder(itemView), View.OnClickListener {

    private val animalImage: ImageView = itemView.findViewById(R.id.animal_row_image)
    private val animalName: TextView = itemView.findViewById(R.id.animal_row_name)
    private val gender: TextView = itemView.findViewById(R.id.gender_name)
    private val age: TextView = itemView.findViewById(R.id.animal_row_approximately_age)
    private val breed: TextView = itemView.findViewById(R.id.animal_row_breed_name)
    private val animalColor: TextView = itemView.findViewById(R.id.animal_row_color)
    private val locationBtn: ImageButton = itemView.findViewById(R.id.animal_row_location_btn)
    private var locationText: TextView = itemView.findViewById(R.id.animal_row_distance_text)
    private val locationSpinner: ProgressBar =
        itemView.findViewById(R.id.animal_row_distance_progress)
    private lateinit var animal: Animal

    init {
        itemView.setOnClickListener(this)
        locationBtn.setOnClickListener {
            onLocationBtnClickListener.clickToLocationBtn()
        }
    }


    fun bind(animal: Animal) {
        this.animal = animal
        val currentContext: Context = animalName.context
        this.animalName.text = animal.name
        this.gender.text = currentContext.getString(R.string.animal_gender, animal.gender)
        this.age.text = currentContext.getString(R.string.animal_age, animal.getAge())
        this.breed.text = currentContext.getString(R.string.animal_breed, animal.breed)
        this.animalColor.text =
            currentContext.getString(R.string.animal_color, animal.characteristics["color"])
        setPrimaryPhoto(animal)
        makeViewByState(animal.locationState ?: LocationState.INIT_STATE, animal.distance)
    }

    fun bindDistanceText(distance: String) {
        locationText.text = distance
    }

    fun makeViewByState(state: LocationState, distance: String) {
        when (state) {
            LocationState.INIT_STATE -> {
                makeInitState()
            }
            LocationState.SEARCH_STATE -> {
                makeSearchState()
            }
            LocationState.DISTANCE_VISIBLE_STATE -> {
                makeDistanceVisibleState(distance)
            }
            else -> {
                makeInitState()
            }
        }
    }

    private fun makeInitState() {
        locationBtn.visibility = View.VISIBLE
        locationText.visibility = View.GONE
        locationSpinner.visibility = View.GONE
    }

    private fun makeSearchState() {
        locationBtn.visibility = View.GONE
        locationText.visibility = View.GONE
        locationSpinner.visibility = View.VISIBLE
    }

    private fun makeDistanceVisibleState(distance: String) {
        locationBtn.visibility = View.GONE
        locationSpinner.visibility = View.GONE
        locationText.visibility = View.VISIBLE
        locationText.text = distance
    }

    override fun onClick(v: View?) {
        onAnimalCardClickListener.onAnimalCardClick(animal)
    }

    private fun setPrimaryPhoto(animal: Animal) {
        val primaryUrl = animal.imgUrl.filter { it.primary }
        val url: String = primaryUrl[0].url
        val endpoint = itemView.resources.getString(R.string.animal_photo_endpoint)
        val dogPlaceholder = animalImage.context.getDrawable(R.drawable.light_dog_placeholder)!!
        networkImageLoader.setPhotoToView(
            animalImage,
            endpoint,
            url,
            dogPlaceholder
        )
    }
}