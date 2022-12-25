package mr.shtein.kennel.presentation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.data.model.KennelPreview
import mr.shtein.data.repository.AppPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.adapter.KennelsAdapter
import mr.shtein.kennel.presentation.state.add_kennel.AddKennelState
import mr.shtein.kennel.presentation.viewmodel.AddKennelViewModel
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.FragmentsListForAssigningAnimation
import mr.shtein.ui_util.setInsetsListenerForPadding
import mr.shtein.ui_util.setStatusBarColor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val LAST_FRAGMENT_KEY = "last_fragment"


class AddKennelFragment : Fragment(R.layout.add_kennel_fragment) {

    companion object {
        private const val KENNEL_ITEM_BUNDLE_KEY = "kennel_item_key"
        private const val SNACK_BAR_VISIBILITY_TIME = 10000
        private const val ANIMATION_DURATION = 300L
    }

    private lateinit var kennelsBtn: TextView
    private lateinit var descriptionView: TextView
    private lateinit var addKennelBtn: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var noKennelsAnimation: LottieAnimationView
    private lateinit var kennelRecycler: RecyclerView
    private lateinit var kennelAdapter: KennelsAdapter
    private val networkImageLoader: ImageLoader by inject()
    private val navigator: KennelNavigation by inject()
    private val appPropertiesRepository: AppPropertiesRepository by inject()
    private val addKennelViewModel: AddKennelViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        navigator.addTransitionsForKennelFlow(this)
        val fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation? =
            arguments?.getParcelable(LAST_FRAGMENT_KEY)
        if (fragmentsListForAssigningAnimation != null) {
            changeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation)
        }
        view?.let {
            setStatusBarColor(true)
            setInsetsListenerForPadding(it, left = false, top = true, right = false, bottom = false)
            initViews(it)
            initRecyclerView(it)
            changeMarginBottom(kennelRecycler)
            setListeners()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addKennelViewModel.addKennelState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddKennelState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is AddKennelState.Success -> {
                    progressBar.visibility = View.INVISIBLE
                    val adapter = kennelRecycler.adapter as KennelsAdapter
                    adapter.setNewData(state.kennelsList)
                }
                is AddKennelState.NoItem -> {
                    animateEmptyKennelsListAnimationAppearance()
                }
                is AddKennelState.Failure -> {
                    progressBar.visibility = View.INVISIBLE
                    val snackBar = Snackbar.make(view, state.message, SNACK_BAR_VISIBILITY_TIME)
                    snackBar.show()
                    snackBar.setAction("Ok") {
                        snackBar.dismiss()
                    }
                }
            }
        }
        addKennelViewModel.loadKennels()
    }

    private fun initViews(view: View) {
        kennelsBtn = view.findViewById(R.id.add_kennel_fragment_kennels_label)
        descriptionView = view.findViewById(R.id.add_kennel_fragment_kennels_or_volunteers_absence)
        addKennelBtn = view.findViewById(R.id.add_kennel_fragment_add_btn)
        progressBar = view.findViewById(R.id.add_kennel_fragment_progressbar)
        noKennelsAnimation = view.findViewById(R.id.add_kennel_empty_kennels_animation)
    }

    private fun animateEmptyKennelsListAnimationAppearance() {
        noKennelsAnimation.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        playAnimation()
                    }
                })

            progressBar.animate()
                .alpha(0f)
                .setDuration(ANIMATION_DURATION)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        progressBar.visibility = View.GONE
                    }
                })
        }
    }

    private fun setListeners() {
        addKennelBtn.setOnClickListener {
            addKennelViewModel.onAddKennelBtnClicked()
        }
    }

    private fun initRecyclerView(view: View) {
        kennelRecycler =
            view.findViewById(R.id.add_kennel_fragment_kennels_or_volunteers_list)
        kennelAdapter = KennelsAdapter(
            listOf(),
            object : KennelsAdapter.OnKennelItemClickListener {
                override fun onClick(kennelItem: KennelPreview) {
                    val kennel = bundleOf(KENNEL_ITEM_BUNDLE_KEY to kennelItem)
                    addKennelViewModel.onKennelItemClicked(kennelItem = kennel)
                }
            },
            networkImageLoader
        )
        kennelRecycler.adapter = kennelAdapter
        kennelRecycler.layoutManager = LinearLayoutManager(
            requireContext(), RecyclerView.VERTICAL, false
        )
    }

    private fun changeAnimationsWhenStartFragment(fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation) {
        when (fragmentsListForAssigningAnimation) {
            FragmentsListForAssigningAnimation.ANIMAL_LIST -> {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            }
            else -> {}
        }
    }

    private fun changeMarginBottom(view: View) {
        val bottomNavHeightInDP = appPropertiesRepository.getBottomNavHeight()
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, bottomNavHeightInDP)
        view.layoutParams = layoutParams
    }
}