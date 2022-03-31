package mr.shtein.buddyandroidclient.screens.kennels

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.AnimalPhotoAdapter
import mr.shtein.buddyandroidclient.model.Animal

private const val ANIMAL_KEY = "animal_key"

class AnimalSettingsFragment: Fragment(R.layout.animal_settings_fragment) {
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: AnimalPhotoAdapter
    private lateinit var name: TextView
    private lateinit var breed: TextView
    private lateinit var age: TextView
    private lateinit var color: TextView
    private lateinit var gender: TextView
    private lateinit var description: TextView
    private lateinit var choiceBtn: ImageButton
    private var animal: Animal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        animal = arguments?.getParcelable(ANIMAL_KEY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view);
        setDataToViews()
    }

    private fun initViews(view: View) {
        photoRecyclerView = view.findViewById(R.id.animal_settings_photo_container)
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        name = view.findViewById(R.id.animal_settings_name)
        breed = view.findViewById(R.id.animal_settings_breed)
        age = view.findViewById(R.id.animal_settings_age)
        color = view.findViewById(R.id.animal_settings_color)
        gender = view.findViewById(R.id.animal_settings_gender)
        description = view.findViewById(R.id.animal_settings_description)
        choiceBtn = view.findViewById(R.id.animal_settings_change_btn)
    }

    private fun setDataToViews() {
        photoRecyclerView.setHasFixedSize(true)
        photoRecyclerView.layoutManager = layoutManager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(photoRecyclerView)
        val animalPhotoInString = animal?.imgUrl?.map {
            it.url
        }
        adapter = AnimalPhotoAdapter(animalPhotoInString ?: listOf())
        photoRecyclerView.adapter = adapter


    }
}
