package mr.shtein.buddyandroidclient

import android.Manifest
import android.content.pm.PackageManager
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
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.*
import mr.shtein.buddyandroidclient.adapters.OnAnimalCardClickListener
import mr.shtein.buddyandroidclient.model.Coordinates
import mr.shtein.buddyandroidclient.model.LocationState
import mr.shtein.buddyandroidclient.utils.AnimalDiffUtil
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import mr.shtein.buddyandroidclient.viewmodels.AnimalListViewModel
import java.net.SocketTimeoutException
import kotlin.math.floor

class AnimalsListFragment : Fragment(), OnAnimalCardClickListener, OnLocationBtnClickListener {

    lateinit var mService: RetrofitServices
    lateinit var adapter: AnimalsAdapter
    lateinit var animalRecyclerView: RecyclerView
    lateinit var allAnimalsList: MutableList<Animal>
    private var visibleAnimalList: MutableList<Animal> = mutableListOf()
    private lateinit var dogChip: Chip
    private lateinit var catChip: Chip
    private lateinit var animalCountText: TextView
    private val animalListViewModel: AnimalListViewModel by lazy {
        ViewModelProvider(this).get(AnimalListViewModel::class.java)
    }

    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var storage: SharedPreferences
    private lateinit var coroutine: CoroutineScope
    private val cancelTokenSourceForLocation = CancellationTokenSource()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        cancelTokenSourceForLocation.token.onCanceledRequested {

        }

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val failureText = getString(R.string.location_failure_text)
            when {
                (permissions.containsKey(Manifest.permission.ACCESS_FINE_LOCATION) ||
                        permissions.containsKey(Manifest.permission.ACCESS_COARSE_LOCATION)) -> {
                    setLocationToView(failureText)
                }
                else -> {
                    Toast.makeText(requireContext(), failureText, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLocationToView(failureText: String) {
        try {
            changeLocationState(LocationState.SEARCH_STATE)
            val locationTask = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancelTokenSourceForLocation.token
            )
            locationTask.addOnSuccessListener {
                val location = locationTask.result
                val currentCoordinates =
                    Coordinates(location.latitude, location.longitude)
                coroutine.launch {
                    try {
                        val token =
                            storage.readString(SharedPreferences.TOKEN_KEY, "")
                        val distances =
                            getDistancesResult(token, currentCoordinates)
                        showResult(distances)
                    } catch (ex: SocketTimeoutException) {
                        changeLocationState(LocationState.BAD_RESULT_STATE)
                        val noInternetText =
                            getString(R.string.internet_failure_text)
                        Toast.makeText(
                            requireContext(), noInternetText, Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            locationTask.addOnFailureListener {
                val error = it.message
                changeLocationState(LocationState.BAD_RESULT_STATE)
                Toast.makeText(requireContext(), failureText, Toast.LENGTH_LONG)
                    .show()
            }
        } catch (ex: SecurityException) {
            Log.d("error", "errorrrrrrrrrrrrInSecurity")
        }
    }

    private suspend fun getDistancesResult(
        token: String,
        coordinates: Coordinates
    ): Response<HashMap<Int, Int>> = withContext(Dispatchers.IO) {
        val retrofit = Common.retrofitService
        return@withContext retrofit.getAllDistances(
            token,
            coordinates.latitude,
            coordinates.longitude
        )
    }

    private suspend fun showResult(result: Response<HashMap<Int, Int>>) =
        withContext(Dispatchers.Main) {
            when (result.code()) {
                200 -> {
                    calculateDiffAndShowResult(result.body() ?: hashMapOf())
                }
            }
        }

    private fun calculateDiffAndShowResult(distances: HashMap<Int, Int>) {
        coroutine.launch {
            val newAnimalList = mutableListOf<Animal>()
            allAnimalsList.forEach { animal ->
                val newAnimal = animal.copy()
                newAnimal.locationState = LocationState.DISTANCE_VISIBLE_STATE
                newAnimalList.add(newAnimal)
                val kennel = animal.kennel
                val distance = distances[kennel.id] ?: 0
                distance.let {
                    if (distance < 1000) {
                        newAnimal.distance = getString(
                            R.string.animal_row_distance_meter_text,
                            distance.toString()
                        )
                    } else {
                        val distanceInKm = floor(distance.toDouble() / 100) / 10
                        newAnimal.distance = getString(
                            R.string.animal_row_distance_kilometer_text,
                            distanceInKm.toString()
                        )
                    }
                }
            }
            val animalDiffUtil = AnimalDiffUtil(allAnimalsList, newAnimalList)
            val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
            adapter.animals = newAnimalList
            animalListViewModel.animalList = newAnimalList
            diffResult.dispatchUpdatesTo(adapter)
        }
    }

    private fun changeLocationState(state: LocationState) {
        val newAnimalList = mutableListOf<Animal>()
        allAnimalsList.forEach { animal ->
            val newAnimal = animal.copy()
            newAnimal.locationState = state
            newAnimalList.add(newAnimal)
        }

        val diffUtil = AnimalDiffUtil(allAnimalsList, newAnimalList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        adapter.animals = newAnimalList
        animalListViewModel.animalList = newAnimalList
        diffResult.dispatchUpdatesTo(adapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutine.cancel()
        cancelTokenSourceForLocation.cancel()
    }

    override fun onClickToLocationBtn() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        coroutine = CoroutineScope(Dispatchers.Main)
        val view = inflater.inflate(R.layout.animals_list_fragment, container, false)
        mService = Common.retrofitService
        animalRecyclerView = view.findViewById(R.id.animal_list)
        animalRecyclerView.setHasFixedSize(true);
        animalRecyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setListeners()
        getAllAnimalsList(view)
    }

    private fun checkLocationPermission() {
        val failureText = getString(R.string.location_failure_text)
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                setLocationToView(failureText)
            }
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                setLocationToView(failureText)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        visibleAnimalList = mutableListOf()
    }

    private fun initViews(view: View) {
        animalCountText = view.findViewById(R.id.animals_list_found_animal_count)
        dogChip = view.findViewById(R.id.animals_list_dog_chip)
        catChip = view.findViewById(R.id.animals_list_cat_chip)
    }

    private fun setViews() {
        animalCountText.text = resources.getQuantityString(
            R.plurals.buddy_found_count,
            visibleAnimalList.size,
            visibleAnimalList.size
        )
    }

    private fun setListeners() {

        dogChip.setOnCheckedChangeListener { _, isChecked ->
            coroutine.launch {
                val dogsList = filterDogs(allAnimalsList)
                when (isChecked) {
                    true -> {
                        val oldAnimalList = visibleAnimalList.toMutableList()
                        visibleAnimalList.addAll(dogsList)
                        visibleAnimalList.shuffle()

                        val animalDiffUtil = AnimalDiffUtil(oldAnimalList, visibleAnimalList)
                        val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
                        adapter.animals = visibleAnimalList
                        diffResult.dispatchUpdatesTo(adapter)
                        setViews()
                    }
                    else -> {
                        val oldAnimalList = visibleAnimalList.toMutableList()
                        visibleAnimalList.removeAll(dogsList)
                        visibleAnimalList.shuffle()

                        val animalDiffUtil = AnimalDiffUtil(oldAnimalList, visibleAnimalList)
                        val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
                        adapter.animals = visibleAnimalList
                        diffResult.dispatchUpdatesTo(adapter)
                        setViews()
                    }
                }
            }
        }

        catChip.setOnCheckedChangeListener { _, isChecked ->
            coroutine.launch {
                val catsList = filterCats(allAnimalsList)
                when (isChecked) {
                    true -> {
                        val oldAnimalList = visibleAnimalList.toMutableList()
                        visibleAnimalList.addAll(catsList)
                        visibleAnimalList.shuffle()

                        val animalDiffUtil = AnimalDiffUtil(oldAnimalList, visibleAnimalList)
                        val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
                        adapter.animals = visibleAnimalList
                        diffResult.dispatchUpdatesTo(adapter)
                        setViews()
                    }
                    else -> {
                        val oldAnimalList = visibleAnimalList.toMutableList()
                        visibleAnimalList.removeAll(catsList)
                        visibleAnimalList.shuffle()

                        val animalDiffUtil = AnimalDiffUtil(oldAnimalList, visibleAnimalList)
                        val diffResult = DiffUtil.calculateDiff(animalDiffUtil)
                        adapter.animals = visibleAnimalList
                        diffResult.dispatchUpdatesTo(adapter)
                        setViews()
                    }
                }
            }
        }
    }

    override fun onAnimalCardClick(animal: Animal) {
        val bundle = Bundle()
        bundle.putParcelable("animal", animal)
        findNavController().navigate(R.id.action_animalsListFragment_to_animalsCardFragment, bundle)
    }

    private fun getAllAnimalsList(view: View) {
        adapter = AnimalsAdapter(
            requireContext(),
            visibleAnimalList,
            this@AnimalsListFragment,
            this@AnimalsListFragment
        )
        animalRecyclerView.adapter = adapter

        if (animalListViewModel.animalList.isEmpty()) {
            mService.getAnimals().enqueue(object : Callback<MutableList<Animal>> {
                override fun onFailure(call: Call<MutableList<Animal>>, t: Throwable) {
                    t.message?.let { Log.d("Animal", it) }
                }

                override fun onResponse(
                    call: Call<MutableList<Animal>>,
                    response: Response<MutableList<Animal>>
                ) {
                    Log.d("Animal", "onResponse is ready")
                    allAnimalsList = response.body() as MutableList<Animal>
                    allAnimalsList.forEach {
                        it.locationState = LocationState.INIT_STATE
                    }
                    animalListViewModel.animalList = allAnimalsList
                    visibleAnimalList.addAll(allAnimalsList)
                    setViews()
                    checkLocationPermission()
                    adapter.notifyItemRangeInserted(0, allAnimalsList.size)
                    view.findViewById<ProgressBar>(R.id.animal_search_progress).visibility =
                        View.INVISIBLE
                }
            })
        } else {
            allAnimalsList = animalListViewModel.animalList
            visibleAnimalList.addAll(animalListViewModel.animalList)
            adapter.notifyItemRangeInserted(0, allAnimalsList.size)
            setViews()
        }

    }

    private suspend fun filterDogs(listForFilter: MutableList<Animal>): MutableList<Animal> =
        withContext(Dispatchers.IO) {
            return@withContext listForFilter.filter {
                it.typeId == 1
            }.toMutableList()
        }

    private suspend fun filterCats(listForFilter: MutableList<Animal>): MutableList<Animal> =
        withContext(Dispatchers.IO) {
            return@withContext listForFilter.filter {
                it.typeId == 2
            }.toMutableList()
        }
}

interface OnLocationBtnClickListener {
    fun onClickToLocationBtn()
}