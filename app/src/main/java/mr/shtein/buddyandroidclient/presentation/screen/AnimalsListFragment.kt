package mr.shtein.buddyandroidclient.presentation.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import mr.shtein.buddyandroidclient.adapters.AnimalsAdapter
import mr.shtein.buddyandroidclient.model.Animal
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.buddyandroidclient.BuddyApplication
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.OnAnimalCardClickListener
import mr.shtein.buddyandroidclient.databinding.AnimalsListFragmentBinding
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalListPresenter
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalsListPresenterImpl
import mr.shtein.buddyandroidclient.setStatusBarColor
import mr.shtein.buddyandroidclient.utils.WorkFragment

private const val LAST_FRAGMENT_KEY = "last_fragment"
private const val ANIMAL_CARD_LABEL = "AnimalsCardFragment"
private const val KENNEL_LABEL = "AddKennelFragment"
private const val USER_PROFILE_LABEL = "UserProfileFragment"
private const val REGISTRATION_LABEL = "UserRegistrationFragment"
private const val LOGIN_LABEL = "LoginFragment"

interface OnLocationBtnClickListener {
    fun clickToLocationBtn()
}

interface AnimalListView {
    fun showAnimals(animalList: List<Animal>)
    fun updateList(animalList: List<Animal>)
    fun checkLocationPermission(): Boolean
    fun setAnimalCountText(animalsAmount: Int)
    fun showError(errorRes: Int)

}

class AnimalsListFragment : Fragment(), OnAnimalCardClickListener, OnLocationBtnClickListener,
    AnimalListView {

    private var _binding: AnimalsListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AnimalsAdapter
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private var animalListPresenter: AnimalListPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTransitAnimations()
        initLocationService()
        initPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val workFragment: WorkFragment? = arguments?.getParcelable(LAST_FRAGMENT_KEY)
        if (workFragment != null) {
            changeAnimationsWhenStartFragment(workFragment)
        }
        _binding = AnimalsListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        animalListPresenter?.onAttachView(this)

        setStatusBarColor(true)
        setInsetsListener(binding.animalChoiceChips)
        initRecyclerView()
        setListeners()
        binding.animalsListSearchProgressBar.visibility = View.VISIBLE
        animalListPresenter?.onAnimalShowCommand(
            binding.animalsListDogChip.isChecked,
            binding.animalsListCatChip.isChecked
        )
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        val appContext = requireContext().applicationContext as BuddyApplication
        appContext.animalListPresenter = animalListPresenter
        animalListPresenter?.onDetachView()
    }

    private fun initPresenter() {
        val appContext = requireContext().applicationContext as BuddyApplication
        if (appContext.animalListPresenter == null) {
            animalListPresenter = AnimalsListPresenterImpl()
            appContext.animalListPresenter = animalListPresenter
        } else {
            animalListPresenter = appContext.animalListPresenter
        }
    }

    private fun initLocationService() {

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                (permissions.containsKey(Manifest.permission.ACCESS_FINE_LOCATION) ||
                        permissions.containsKey(Manifest.permission.ACCESS_COARSE_LOCATION)) -> {
                    animalListPresenter?.onClickToLocationBtn()
                }
                else -> {
                    val failureText = getString(R.string.location_failure_text)
                    Toast.makeText(requireContext(), failureText, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initTransitAnimations() {
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            val workFragment = makeWorkFragment(destination)
            changeAnimationsWhenNavigate(workFragment)
        }
        if (arguments != null) {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        } else {
            val enterSlide = Slide()
            enterSlide.slideEdge = Gravity.RIGHT
            enterSlide.duration = 300
            enterSlide.interpolator = DecelerateInterpolator()
            enterTransition = enterSlide
        }
    }

    private fun makeWorkFragment(destination: NavDestination): WorkFragment {
        return when (destination.label) {
            ANIMAL_CARD_LABEL -> WorkFragment.ANIMAL_CARD
            KENNEL_LABEL -> WorkFragment.ADD_KENNEL
            USER_PROFILE_LABEL -> WorkFragment.USER_PROFILE
            REGISTRATION_LABEL -> WorkFragment.REGISTRATION
            LOGIN_LABEL -> WorkFragment.LOGIN
            else -> WorkFragment.OTHER
        }
    }

    override fun updateList(animalList: List<Animal>) {
        adapter.updateAnimalList(animalList)
    }

    private fun initRecyclerView() {
        binding.animalList.setHasFixedSize(true)
        binding.animalList.layoutManager = LinearLayoutManager(context)
        adapter = AnimalsAdapter(
            requireContext(),
            listOf(),
            this@AnimalsListFragment,
            this@AnimalsListFragment
        )
        binding.animalList.adapter = adapter
    }

    override fun showAnimals(animalList: List<Animal>) {
        adapter.updateAnimalList(animalList)
        binding.animalsListSearchProgressBar.visibility = View.INVISIBLE
    }

    override fun setAnimalCountText(animalsAmount: Int) {
        binding.animalsListFoundAnimalCount.text = resources.getQuantityString(
            R.plurals.buddy_found_count,
            animalsAmount,
            animalsAmount
        )
    }

    private fun setInsetsListener(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    override fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED
                || coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun clickToLocationBtn() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun setListeners() {

        binding.animalsListDogChip.setOnCheckedChangeListener { _, isDogChecked ->
            val isCatChecked = binding.animalsListCatChip.isChecked
            animalListPresenter?.onAnimalShowCommand(isDogChecked, isCatChecked)
        }

        binding.animalsListCatChip.setOnCheckedChangeListener { _, isCatChecked ->
            val isDogChecked = binding.animalsListDogChip.isChecked
            animalListPresenter?.onAnimalShowCommand(isDogChecked, isCatChecked)
        }
    }

    override fun onAnimalCardClick(animal: Animal) {
        val bundle = Bundle()
        bundle.putParcelable("animal", animal)
        changeAnimationsWhenNavigate(WorkFragment.ANIMAL_CARD)
        findNavController().navigate(R.id.action_animalsListFragment_to_animalsCardFragment, bundle)
    }

    override fun showError(errorRes: Int) {
        val errorText = getString(errorRes)
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }

    private fun changeAnimationsWhenStartFragment(workFragment: WorkFragment) {
        when (workFragment) {
            WorkFragment.ADD_KENNEL -> {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            }
            WorkFragment.USER_PROFILE -> {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            }
            WorkFragment.LOGIN -> {
                val enterSlide = Slide()
                enterSlide.slideEdge = Gravity.RIGHT
                enterSlide.duration = 300
                enterSlide.interpolator = DecelerateInterpolator()
                enterTransition = enterSlide
            }
            else -> {}
        }
    }

    private fun changeAnimationsWhenNavigate(workFragment: WorkFragment) {
        when (workFragment) {
            WorkFragment.ANIMAL_CARD -> {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            }
            WorkFragment.ADD_KENNEL -> {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            }
            WorkFragment.USER_PROFILE -> {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            }
            else -> {
                val exitSlide = Slide()
                exitSlide.slideEdge = Gravity.LEFT
                exitSlide.duration = 300
                exitSlide.interpolator = DecelerateInterpolator()
                exitTransition = exitSlide
            }
        }
    }
}







