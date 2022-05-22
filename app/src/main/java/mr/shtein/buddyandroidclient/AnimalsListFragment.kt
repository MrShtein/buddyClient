package mr.shtein.buddyandroidclient

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import mr.shtein.buddyandroidclient.adapters.AnimalsAdapter
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.retrofit.RetrofitServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.TypedValue

import android.content.res.Resources
import android.view.*
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.adapters.OnAnimalCardClickListener
import mr.shtein.buddyandroidclient.utils.AnimalDiffUtil

class AnimalsListFragment : Fragment(), OnAnimalCardClickListener {

    lateinit var mService: RetrofitServices
    lateinit var adapter: AnimalsAdapter
    lateinit var animalRecyclerView: RecyclerView
    lateinit var animals: MutableList<Animal>
    private var currentAnimalsList: MutableList<Animal> = mutableListOf()
    private var oldAnimalList: MutableList<Animal> = mutableListOf()
    private var dogsList: MutableList<Animal> = mutableListOf()
    private var catsList: MutableList<Animal> = mutableListOf()
    private lateinit var dogChip: Chip
    private lateinit var catChip: Chip
    private lateinit var animalCountText: TextView
    private val coroutine = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.animals_list_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mService = Common.retrofitService
        coroutine.launch {
            animalRecyclerView = view.findViewById(R.id.animal_list)
            animalRecyclerView.setHasFixedSize(true);
            animalRecyclerView.layoutManager = LinearLayoutManager(context)
            getAllAnimalsList(view)
        }
        initViews(view)
        setListeners()
    }

    override fun onStop() {
        super.onStop()
        currentAnimalsList = mutableListOf()
    }

    private fun initViews(view: View) {
        animalCountText = view.findViewById(R.id.animals_list_found_animal_count)
        dogChip = view.findViewById(R.id.animals_list_dog_chip)
        catChip = view.findViewById(R.id.animals_list_cat_chip)
    }

    private fun setViews() {
        animalCountText.text = resources.getQuantityString(
            R.plurals.buddy_found_count,
            currentAnimalsList.size,
            currentAnimalsList.size
        )
    }

    private fun setListeners() {

        dogChip.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> {
                    oldAnimalList = currentAnimalsList.toMutableList()
                    currentAnimalsList.addAll(dogsList)
                    currentAnimalsList.shuffle()

                    val animalDiffUtil = AnimalDiffUtil(oldAnimalList, currentAnimalsList)
                    val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
                    diffResult.dispatchUpdatesTo(adapter)
                    setViews()
                }
                else -> {
                    oldAnimalList = currentAnimalsList.toMutableList()
                    currentAnimalsList.removeAll(dogsList)

                    val animalDiffUtil = AnimalDiffUtil(oldAnimalList, currentAnimalsList)
                    val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
                    diffResult.dispatchUpdatesTo(adapter)
                    setViews()
                }
            }
        }

        catChip.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> {
                    oldAnimalList = currentAnimalsList.toMutableList()
                    currentAnimalsList.addAll(catsList)
                    currentAnimalsList.shuffle()

                    val animalDiffUtil = AnimalDiffUtil(oldAnimalList, currentAnimalsList)
                    val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
                    diffResult.dispatchUpdatesTo(adapter)
                    setViews()

                }
                else -> {
                    oldAnimalList = currentAnimalsList.toMutableList()
                    currentAnimalsList.removeAll(catsList)

                    val animalDiffUtil = AnimalDiffUtil(oldAnimalList, currentAnimalsList)
                    val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
                    diffResult.dispatchUpdatesTo(adapter)
                    setViews()
                }
            }
        }

    }

    override fun onAnimalCardClick(animal: Animal) {
        val bundle = Bundle()
        bundle.putParcelable("animal", animal)
        findNavController().navigate(R.id.action_animalsListFragment_to_animalsCardFragment, bundle)
    }

    private fun dpToPx(dp: Int, view: View): Int {
        val r: Resources = view.context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).toInt()
    }

    private fun getAllAnimalsList(view: View) {
        mService.getAnimals().enqueue(object : Callback<MutableList<Animal>> {
            override fun onFailure(call: Call<MutableList<Animal>>, t: Throwable) {
                t.message?.let { Log.d("Animal", it) }
            }

            override fun onResponse(
                call: Call<MutableList<Animal>>,
                response: Response<MutableList<Animal>>
            ) {
                Log.d("Animal", "onResponse is ready")
                animals = response.body() as MutableList<Animal>
                currentAnimalsList.addAll(animals)
                setViews()
                coroutine.launch {
                    filterCats()
                    filterDogs()
                }
                adapter = AnimalsAdapter(
                    requireContext(),
                    currentAnimalsList,
                    this@AnimalsListFragment
                )
                adapter.notifyDataSetChanged()
                view.findViewById<ProgressBar>(R.id.animal_search_progress).visibility =
                    View.INVISIBLE
                animalRecyclerView.adapter = adapter
            }
        })
    }

    private suspend fun filterDogs() = withContext(Dispatchers.IO) {
        dogsList = animals.filter {
            it.typeId == 1
        }.toMutableList()
    }

    private suspend fun filterCats() = withContext(Dispatchers.IO) {
        catsList = animals.filter {
            it.typeId == 2
        }.toMutableList()
    }


}