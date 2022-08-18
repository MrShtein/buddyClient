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
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.buddyandroidclient.*
import mr.shtein.buddyandroidclient.adapters.OnAnimalCardClickListener
import mr.shtein.buddyandroidclient.databinding.AnimalsListFragmentBinding
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalListPresenter
import mr.shtein.buddyandroidclient.utils.FragmentsListForAssigningAnimation
import org.koin.android.ext.android.inject

private const val LAST_FRAGMENT_KEY = "last_fragment"

interface OnLocationBtnClickListener {
    fun clickToLocationBtn()
}

interface AnimalListView {
    fun updateList(animalList: List<Animal>)
    fun checkLocationPermission(): Boolean
    fun setAnimalCountText(animalsAmount: Int)
    fun showError(errorRes: Int)

    fun setAnimationWhenToAnimalCardNavigate()
    fun setAnimationWhenToAddKennelNavigate()
    fun setAnimationWhenToUserProfileNavigate()
    fun setAnimationWhenToOtherFragmentNavigate()
    fun setAnimationWhenUserComeFromAddKennel()
    fun setAnimationWhenUserComeFromUserProfile()
    fun setAnimationWhenUserComeFromLogin()


    fun setUpView()
    fun setAnimationWhenUserComeFromCity()
    fun setAnimationWhenUserComeFromSplash()
    fun showAnimalSearchProgressBar()
    fun hideAnimalSearchProgressBar()
}

class AnimalsListFragment : Fragment(), OnAnimalCardClickListener, OnLocationBtnClickListener,
    AnimalListView {

    private var _binding: AnimalsListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AnimalsAdapter
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private val animalListPresenter: AnimalListPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation? =
            arguments?.getParcelable(LAST_FRAGMENT_KEY)
        _binding = AnimalsListFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        animalListPresenter.onAttachView(this)
        animalListPresenter.onChangeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation)
        animalListPresenter.onAnimalShowCommand(
            binding.animalsListDogChip.isChecked,
            binding.animalsListCatChip.isChecked,
            false
        )
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        animalListPresenter.onDetachView()
    }

    override fun showAnimalSearchProgressBar() {
        binding.animalsListSearchProgressBar.visibility = View.VISIBLE
    }

    override fun hideAnimalSearchProgressBar() {
        binding.animalsListSearchProgressBar.visibility = View.INVISIBLE
    }

    override fun setUpView() {
        changeMarginBottom(binding.animalsListSwipeLayout, requireActivity() as MainActivity)
        initLocationService()
        setStatusBarColor(true)
        initRecyclerView()
        setListeners()
    }

    override fun updateList(animalList: List<Animal>) {
        val previousListSize = adapter.updateAnimalList(animalList)
        binding.animalsListSearchProgressBar.visibility = View.INVISIBLE
        binding.animalsListSwipeLayout.isRefreshing = false
        animalListPresenter.onUpdatedList(animalList, previousListSize)
    }

    override fun setAnimalCountText(animalsAmount: Int) {
        binding.animalsListFoundAnimalCount.text = resources.getQuantityString(
            R.plurals.buddy_found_count,
            animalsAmount,
            animalsAmount
        )
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

    override fun onAnimalCardClick(animal: Animal) {
        val bundle = Bundle()
        bundle.putParcelable("animal", animal)
        animalListPresenter.onChangeAnimationsWhenNavigate(FragmentsListForAssigningAnimation.ANIMAL_CARD)
        findNavController().navigate(R.id.action_animalsListFragment_to_animalsCardFragment, bundle)
    }

    override fun showError(errorRes: Int) {
        val errorText = getString(errorRes)
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }

    override fun setAnimationWhenUserComeFromAddKennel() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
    }

    override fun setAnimationWhenUserComeFromUserProfile() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun setAnimationWhenUserComeFromLogin() {
        val enterSlide = Slide()
        enterSlide.slideEdge = Gravity.RIGHT
        enterSlide.duration = 300
        enterSlide.interpolator = DecelerateInterpolator()
        enterTransition = enterSlide
    }

    override fun setAnimationWhenUserComeFromCity() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun setAnimationWhenUserComeFromSplash() {
        val enterSlide = Slide()
        enterSlide.slideEdge = Gravity.RIGHT
        enterSlide.duration = 300
        enterSlide.interpolator = DecelerateInterpolator()
        enterTransition = enterSlide
    }

    override fun setAnimationWhenToAnimalCardNavigate() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun setAnimationWhenToAddKennelNavigate() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
    }

    override fun setAnimationWhenToUserProfileNavigate() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun setAnimationWhenToOtherFragmentNavigate() {
        val exitSlide = Slide()
        exitSlide.slideEdge = Gravity.LEFT
        exitSlide.duration = 300
        exitSlide.interpolator = DecelerateInterpolator()
        exitTransition = exitSlide
    }

    private fun initPresenter() {
        val appContext = requireContext().applicationContext as BuddyApplication
        if (appContext.animalListPresenter == null) {
            appContext.animalListPresenter = animalListPresenter
        }
    }

    private fun initLocationService() {

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                (permissions.containsKey(Manifest.permission.ACCESS_FINE_LOCATION) ||
                        permissions.containsKey(Manifest.permission.ACCESS_COARSE_LOCATION)) -> {
                    animalListPresenter.onClickToLocationBtn()
                }
                else -> {
                    val failureText = getString(R.string.location_failure_text)
                    Toast.makeText(requireContext(), failureText, Toast.LENGTH_LONG).show()
                }
            }
        }
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

    private fun setInsetsListener(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setListeners() {

        binding.animalsListDogChip.setOnCheckedChangeListener { _, isDogChecked ->
            val isCatChecked = binding.animalsListCatChip.isChecked
            animalListPresenter.onAnimalShowCommand(isDogChecked, isCatChecked)
        }

        binding.animalsListCatChip.setOnCheckedChangeListener { _, isCatChecked ->
            val isDogChecked = binding.animalsListDogChip.isChecked
            animalListPresenter.onAnimalShowCommand(isDogChecked, isCatChecked)
        }

        binding.animalsListSwipeLayout.setOnRefreshListener {
            val isCatChecked = binding.animalsListCatChip.isChecked
            val isDogChecked = binding.animalsListDogChip.isChecked
            animalListPresenter.onAnimalShowCommand(isDogChecked, isCatChecked)
        }

        setInsetsListener(binding.animalChoiceChips)

        findNavController().addOnDestinationChangedListener { _, navDestination, _ ->
            val destination = navDestination.label.toString()
            val fragmentsListForAssigningAnimation =
                animalListPresenter.onGetListForAssigningAnimation(destination)
            animalListPresenter.onChangeAnimationsWhenNavigate(fragmentsListForAssigningAnimation)
        }
    }




}







