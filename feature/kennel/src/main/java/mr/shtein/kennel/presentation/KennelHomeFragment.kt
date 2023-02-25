package mr.shtein.kennel.presentation

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.*
import mr.shtein.data.model.Animal
import mr.shtein.kennel.R
import mr.shtein.kennel.presentation.adapter.CatPhotoAdapter
import mr.shtein.kennel.presentation.adapter.DogPhotoAdapter
import mr.shtein.kennel.presentation.state.kennel_home.AnimalListState
import mr.shtein.kennel.presentation.state.kennel_home.KennelHomeUiState
import mr.shtein.util.state.VolunteerBidsState
import mr.shtein.kennel.presentation.viewmodel.KennelHomeViewModel
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.setStatusBarColor
import mr.shtein.util.CommonViewModel
import mr.shtein.util.showMessage
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val KENNEL_ITEM_BUNDLE_KEY = "kennel_item_key"
private const val ANIMAL_BUNDLE_KEY = "animal_bundle_key"
private const val NEW_ANIMAL_ADDED_RESULT_KEY = "new_animal_added"

class KennelHomeFragment : Fragment(R.layout.kennel_home_fragment) {

    private lateinit var kennelAvatar: ImageView
    private lateinit var kennelName: TextView
    private lateinit var volunteersAmount: TextView
    private lateinit var animalsAmount: TextView
    private lateinit var dogsAmount: TextView
    private lateinit var addDogsBtn: ImageButton
    private lateinit var dogCarousel: RecyclerView
    private lateinit var catsAmount: TextView
    private lateinit var addCatsBtn: ImageButton
    private lateinit var volunteersBtn: MaterialButton
    private lateinit var catCarousel: RecyclerView
    private lateinit var dogAdapter: DogPhotoAdapter
    private lateinit var catAdapter: CatPhotoAdapter
    private lateinit var badge: BadgeDrawable
    private val networkImageLoader: ImageLoader by inject()
    private val kennelHomeViewModel: KennelHomeViewModel by viewModel {
        parametersOf(
            arguments?.getParcelable(KENNEL_ITEM_BUNDLE_KEY)
        )
    }
    private val commonViewModel: CommonViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        setFragmentResultListener(NEW_ANIMAL_ADDED_RESULT_KEY) { _, bundle ->
            val animal: Animal = bundle.getParcelable(ANIMAL_BUNDLE_KEY)!!
            kennelHomeViewModel.onNewAnimalAdded(animal = animal)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor(false)
        initViews(view)
        makeDogRecyclerView()
        makeCatRecyclerView()
        setListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                kennelHomeViewModel.volunteerBidsState.collect { volunteersBtnState ->
                    when (volunteersBtnState) {
                        is VolunteerBidsState.Failure -> {
                            val errorMessage: String = getString(volunteersBtnState.messageResId)
                            showMessage(message = errorMessage)
                        }
                        is VolunteerBidsState.Success -> {
                            @ExperimentalBadgeUtils
                            if (volunteersBtnState.bids.isNotEmpty()) {
                                showBidBtnBadge(volunteersBtnState.bids.size)
                                commonViewModel.updateBidsByKennel(
                                    kennelId = volunteersBtnState.bids[0].kennelId,
                                    volunteerBidsList = volunteersBtnState.bids
                                )
                            }
                        }
                        VolunteerBidsState.Loading -> {}
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                kennelHomeViewModel.kennelHomeUiState.collect { kennelHomeHeaderUiState ->
                    if (kennelHomeHeaderUiState.isDialogVisible) {
                        showKennelIsNotValidDialog()
                    } else {
                        setHeaderValuesToView(kennelHomeHeaderUiState)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                kennelHomeViewModel.dogListState.collect { dogListState ->
                    when (dogListState) {
                        is AnimalListState.Success -> {
                            if (dogListState.animalList.isNotEmpty()) {
                                dogCarousel.visibility = View.VISIBLE
                            }
                            val dogAdapter = dogCarousel.adapter as DogPhotoAdapter
                            dogAdapter.setAnimalList(dogListState.animalList)
                            dogsAmount.text = getAnimalCountText(dogAdapter.itemCount)
                        }
                        is AnimalListState.Failure -> {
                            val errorMessage: String = getString(dogListState.messageResId)
                            showError(errorText = errorMessage)
                        }
                        AnimalListState.Loading -> {
                            //TODO Придумать дизайнеру, что делать во время загрузки
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                kennelHomeViewModel.catListState.collect { catListState ->
                    when (catListState) {
                        is AnimalListState.Success -> {
                            if (catListState.animalList.isNotEmpty()) {
                                catCarousel.visibility = View.VISIBLE
                            }
                            val catAdapter = catCarousel.adapter as CatPhotoAdapter
                            catAdapter.setAnimalList(catListState.animalList)
                            catsAmount.text = getAnimalCountText(catAdapter.itemCount)
                        }
                        is AnimalListState.Failure -> {
                            val errorMessage: String = getString(catListState.messageResId)
                            showError(errorText = errorMessage)
                        }
                        AnimalListState.Loading -> {
                            //TODO Придумать дизайнеру, что делать во время загрузки
                        }
                    }
                }
            }
        }
    }

    @ExperimentalBadgeUtils
    private fun showBidBtnBadge(size: Int) {
        badge.number = size
        volunteersBtn.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                BadgeUtils.attachBadgeDrawable(badge, volunteersBtn)
                volunteersBtn.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun initViews(view: View) {
        kennelAvatar = view.findViewById(R.id.kennel_home_avatar)
        kennelName = view.findViewById(R.id.kennel_home_name)
        volunteersAmount = view.findViewById(R.id.kennel_home_volunteers_amount)
        animalsAmount = view.findViewById(R.id.kennel_home_animals_amount)
        dogsAmount = view.findViewById(R.id.kennel_home_dogs_amount)
        addDogsBtn = view.findViewById(R.id.kennel_home_add_dogs_btn)
        dogCarousel = view.findViewById(R.id.kennel_home_dog_carousel)
        catsAmount = view.findViewById(R.id.kennel_home_cats_amount)
        addCatsBtn = view.findViewById(R.id.kennel_home_add_cats_btn)
        volunteersBtn = view.findViewById(R.id.kennel_home_volunteers_btn)
        catCarousel = view.findViewById(R.id.kennel_home_cat_carousel)
        dogCarousel = view.findViewById(R.id.kennel_home_dog_carousel)
        badge = BadgeDrawable.createFromResource(requireContext(), R.xml.badge_item)
    }

    private fun setHeaderValuesToView(headerUiState: KennelHomeUiState) {
        val endpoint = getString(R.string.kennel_avatar_endpoint)
        val photoName = headerUiState.kennelAvatarUrl
        val dogPlaceholder = context?.getDrawable(R.drawable.dog_placeholder)
        networkImageLoader.setPhotoToView(
            kennelAvatar,
            endpoint,
            photoName,
            dogPlaceholder
        )
        kennelName.text = headerUiState.kennelName
        volunteersAmount.text = makeVolunteersText(headerUiState.volunteersAmount)
        animalsAmount.text = getAnimalCountText(headerUiState.animalsAmount)
    }

    private fun makeCatRecyclerView() {
        catCarousel.setHasFixedSize(true)
        catAdapter = CatPhotoAdapter(
            listOf(),
            object : CatPhotoAdapter.OnAnimalItemClickListener {
                override fun onClick(animalItem: Animal) {
                    kennelHomeViewModel.onAnimalPhotoClick(animalItem = animalItem)
                }
            },
            networkImageLoader
        )
        catCarousel.adapter = catAdapter
        catCarousel.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val catCarouselHelper = LinearSnapHelper()
        catCarouselHelper.attachToRecyclerView(catCarousel)
    }

    private fun makeDogRecyclerView() {
        dogCarousel.setHasFixedSize(true)
        dogAdapter = DogPhotoAdapter(
            listOf(),
            object : DogPhotoAdapter.OnAnimalItemClickListener {
                override fun onClick(animalItem: Animal) {
                    kennelHomeViewModel.onAnimalPhotoClick(animalItem = animalItem)
                }
            },
            networkImageLoader
        )

        dogCarousel.adapter = dogAdapter
        dogCarousel.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dogCarouselHelper = LinearSnapHelper()
        dogCarouselHelper.attachToRecyclerView(dogCarousel)
    }

    private fun setListeners() {
        addDogsBtn.setOnClickListener {
            kennelHomeViewModel.onAddDogBtnClick()
        }

        addCatsBtn.setOnClickListener {
            kennelHomeViewModel.onCatDogBtnClick()
        }

        volunteersBtn.setOnClickListener {
            kennelHomeViewModel.onVolunteersBtnClick()
        }
    }

    private fun getAnimalCountText(amount: Int): String {
        return resources.getQuantityString(R.plurals.buddy_found_count, amount, amount)
    }

    private fun makeVolunteersText(amount: Int): String {
        return resources.getQuantityString(R.plurals.volunteers_found_count, amount, amount)
    }

    private fun showKennelIsNotValidDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)
            .setView(R.layout.not_valid_kennel_dialog)
            .show()
        val okBtn: Button? = dialog.findViewById(R.id.not_valid_kennel_dialog_ok_btn)
        okBtn?.setOnClickListener {
            dialog.cancel()
            kennelHomeViewModel.onCancelDialog()
        }
    }

    private fun showError(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }
}