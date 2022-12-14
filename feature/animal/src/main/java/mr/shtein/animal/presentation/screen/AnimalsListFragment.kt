package mr.shtein.animal.presentation.screen

import android.Manifest
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialSharedAxis
import moxy.MvpAppCompatFragment
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.OneExecution
import mr.shtein.animal.R
import mr.shtein.animal.databinding.AnimalsListFragmentBinding
import mr.shtein.animal.navigation.AnimalNavigation
import mr.shtein.animal.presentation.adapter.AnimalsAdapter
import mr.shtein.animal.presentation.listener.OnAnimalCardClickListener
import mr.shtein.animal.presentation.presenter.AnimalsListPresenterImpl
import mr.shtein.data.model.Animal
import mr.shtein.data.model.AnimalFilter
import mr.shtein.data.repository.AppPropertiesRepository
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.FragmentsListForAssigningAnimation
import mr.shtein.ui_util.setStatusBarColor
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject


private const val LAST_FRAGMENT_KEY = "last_fragment"
private const val ANIMAL_FILTER_KEY = "animal_filter"

interface OnLocationBtnClickListener {
    fun clickToLocationBtn()
}

@StateStrategyType(AddToEndSingleStrategy::class)
interface AnimalListView : MvpView {
    fun updateList(animalList: List<Animal>)

    fun setAnimationWhenToAnimalCardNavigate()
    fun setAnimationWhenToAddKennelNavigate()
    fun setAnimationWhenToUserProfileNavigate()
    fun setAnimationWhenToOtherFragmentNavigate()

    fun setAnimationWhenUserComeFromAddKennel()
    fun setAnimationWhenUserComeFromUserProfile()
    fun setAnimationWhenUserComeFromLogin()
    fun setAnimationWhenUserComeFromSplash()
    fun setAnimationWhenUserComeFromCity()
    fun setAnimationWhenToAnimalFilterNavigate()

    fun toggleAnimalSearchProgressBar(isVisible: Boolean)

    @OneExecution
    fun toggleDogChip(isChecked: Boolean)

    @OneExecution
    fun toggleCatChip(isChecked: Boolean)

    fun showAnimalCountText(animalsAmount: Int)

    @OneExecution
    fun navigateToAnimalFilter(animalFilter: AnimalFilter)

    @OneExecution
    fun showLocationFailureText(locationFailureText: Int)

    @OneExecution
    fun showError(errorRes: Int)

    fun showFilterBadge(filterItemsCount: Int, badge: BadgeDrawable)

    fun hideFilterBadge(badge: BadgeDrawable)
}

class AnimalsListFragment : MvpAppCompatFragment(), OnAnimalCardClickListener,
    OnLocationBtnClickListener, AnimalListView {

    private var _binding: AnimalsListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AnimalsAdapter
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private val networkImageLoader: ImageLoader by inject()
    private val navigator: AnimalNavigation by inject()
    private val appPropertiesRepository: AppPropertiesRepository by inject()
    private var onDestinationChangedListener: NavController.OnDestinationChangedListener? = null

    @InjectPresenter
    lateinit var animalListPresenter: AnimalsListPresenterImpl

    @ProvidePresenter
    fun providePresenter(): AnimalsListPresenterImpl {
        return get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocationService()
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
        setUpView()

        animalListPresenter.onChangeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation)
        animalListPresenter.onAnimalShowCommand(false)
        animalListPresenter.onChipsReadyToToggle()
        return view
    }

    override fun showFilterBadge(filterItemsCount: Int, badge: BadgeDrawable) {
        binding.animalsListFilterBtn.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                badge.number = filterItemsCount
                BadgeUtils.attachBadgeDrawable(badge, binding.animalsListFilterBtn)
                binding.animalsListFilterBtn.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun hideFilterBadge(badge: BadgeDrawable) {
        BadgeUtils.detachBadgeDrawable(badge, binding.animalsListFilterBtn)
    }

    override fun toggleAnimalSearchProgressBar(isVisible: Boolean) {
        binding.animalsListSearchProgressBar.isVisible = isVisible
    }

    override fun toggleDogChip(isChecked: Boolean) {
        binding.animalsListDogChip.isChecked = isChecked
    }

    override fun toggleCatChip(isChecked: Boolean) {
        binding.animalsListCatChip.isChecked = isChecked
    }

    override fun updateList(animalList: List<Animal>) {
        val previousListSize = adapter.updateAnimalList(animalList)
        binding.animalsListSearchProgressBar.visibility = View.INVISIBLE
        binding.animalsListSwipeLayout.isRefreshing = false
        animalListPresenter.onUpdatedList(animalList, previousListSize)
    }

    override fun showAnimalCountText(animalsAmount: Int) {
        binding.animalsListFoundAnimalCount.text = resources.getQuantityString(
            R.plurals.buddy_found_count,
            animalsAmount,
            animalsAmount
        )
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
        navigator.moveToAnimalCardFromAnimalList(animal)
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

    override fun setAnimationWhenToAnimalFilterNavigate() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun setAnimationWhenToOtherFragmentNavigate() {
        val exitSlide = Slide()
        exitSlide.slideEdge = Gravity.LEFT
        exitSlide.duration = 300
        exitSlide.interpolator = DecelerateInterpolator()
        exitTransition = exitSlide
    }

    override fun showLocationFailureText(locationFailureText: Int) {
        val failureText = getString(locationFailureText)
        Toast.makeText(requireContext(), failureText, Toast.LENGTH_LONG).show()
    }

    override fun navigateToAnimalFilter(animalFilter: AnimalFilter) {
        navigator.moveToAnimalFilterFromAnimalList(animalFilter)
    }

    private fun setUpView() {
        changeMarginBottom(binding.animalsListSwipeLayout)
        setStatusBarColor(true)
        initRecyclerView()
        setListeners()
    }

    private fun initPresenter() {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val badge = BadgeDrawable.createFromResource(requireContext(), R.xml.filter_badge_item)
        val animalFilter =
            arguments?.getParcelable(ANIMAL_FILTER_KEY) ?: AnimalFilter()
        animalListPresenter.onInit(
            fineLocationPermission,
            coarseLocationPermission,
            animalFilter,
            badge
        )
    }

    private fun initLocationService() {
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            animalListPresenter.onClickToLocationBtn(permissions)
        }
    }

    private fun initRecyclerView() {
        binding.animalList.setHasFixedSize(true)
        binding.animalList.layoutManager = LinearLayoutManager(context)
        adapter = AnimalsAdapter(
            requireContext(),
            listOf(),
            this@AnimalsListFragment,
            this@AnimalsListFragment,
            networkImageLoader
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

    private fun onFilterBtnClick() {
        animalListPresenter.onFilterBtnClicked()
    }

    private fun setListeners() {

        binding.animalsListDogChip.setOnClickListener {
            val isDogChecked = (it as Chip).isChecked
            animalListPresenter.onDogChipClicked(isDogChecked)
            animalListPresenter.onAnimalShowCommand(getFromNetwork = true)
        }

        binding.animalsListCatChip.setOnClickListener {
            val isCatChecked = (it as Chip).isChecked
            animalListPresenter.onCatChipClicked(isCatChecked)
            animalListPresenter.onAnimalShowCommand(getFromNetwork = true)
        }

        binding.animalsListSwipeLayout.setOnRefreshListener {
            animalListPresenter.onAnimalShowCommand(getFromNetwork = true)
        }

        binding.animalsListFilterBtn.setOnClickListener {
            animalListPresenter.onFilterBtnClicked()
        }

        setInsetsListener(binding.animalChoiceChips)
        setInsetsListener(binding.animalsListFilterBtn)

        onDestinationChangedListener =
            NavController.OnDestinationChangedListener { _, navDestination, _ ->
                val destination = navDestination.label.toString()
                val fragmentsListForAssigningAnimation =
                    animalListPresenter.onGetListForAssigningAnimation(destination)
                animalListPresenter.onChangeAnimationsWhenNavigate(
                    fragmentsListForAssigningAnimation
                )
            }
        onDestinationChangedListener?.let {
            navigator.addOnDestinationChangeListener(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestinationChangedListener?.let {
            navigator.removeDestinationChangeListener(it)
        }
    }

    private fun changeMarginBottom(view: View) {
        val bottomNavHeightInDP = appPropertiesRepository.getBottomNavHeight()
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, bottomNavHeightInDP)
        view.layoutParams = layoutParams
    }
}







