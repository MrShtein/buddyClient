package mr.shtein.kennel.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mr.shtein.data.model.KennelPreview
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.databinding.VolunteersListFragmentBinding
import mr.shtein.kennel.presentation.adapter.VolunteerAndBidAdapter
import mr.shtein.kennel.presentation.state.volunteers_list.VolunteersListBodyState
import mr.shtein.kennel.presentation.viewmodel.VolunteerListViewModel
import mr.shtein.model.volunteer.VolunteerDTO
import mr.shtein.model.volunteer.VolunteersBid
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.setInsetsListenerForPadding
import mr.shtein.ui_util.setStatusBarColor
import mr.shtein.util.changePadding
import mr.shtein.util.showMessage
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VolunteersListFragment : Fragment(R.layout.volunteers_list_fragment) {

    companion object {
        private const val KENNEL_ITEM_BUNDLE_KEY = "kennel_item_key"
    }

    private var _binding: VolunteersListFragmentBinding? = null
    private val binding
        get() = _binding!!
    private val volunteerViewModel: VolunteerListViewModel by viewModel {
        parametersOf(arguments?.getParcelable(KENNEL_ITEM_BUNDLE_KEY), "")
    }
    private val imageLoader: ImageLoader by inject()
    private val userPropertiesRepository: UserPropertiesRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = VolunteersListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor(true)
        initRecyclerView()

        setInsetsListenerForPadding(
            binding.volunteersListHeader,
            left = false,
            top = true,
            right = false,
            bottom = false
        )
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                volunteerViewModel.volunteerListState.collect { volunteersListState ->
                    showKennelName(volunteersListState.kennelName)
                    val volunteersListBodyState: VolunteersListBodyState =
                        volunteersListState.volunteersListBodyState
                    when (volunteersListBodyState) {
                        VolunteersListBodyState.Loading -> {
                            binding.volunteersListProgressBar.isVisible = true
                        }
                        is VolunteersListBodyState.Failure -> {
                            val errorMessage: String = getString(volunteersListBodyState.message)
                            showMessage(errorMessage)
                            binding.volunteersListProgressBar.isGone = true
                        }
                        VolunteersListBodyState.NoItem -> {}
                        is VolunteersListBodyState.Success -> {
                            binding.volunteersListProgressBar.isGone = true
                            val recyclerAdapter =
                                binding.volunteersListBody.adapter as VolunteerAndBidAdapter
                            recyclerAdapter.updateItems(
                                volunteersListBodyState.volunteersList.toMutableList()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        with(binding.volunteersListBody) {
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                changePadding(bottom = insets.bottom)
                WindowInsetsCompat.CONSUMED
            }
            setHasFixedSize(true)
            val volunteerAndBidAdapter = VolunteerAndBidAdapter(
                onVolunteerItemClick = object :
                    VolunteerAndBidAdapter.OnVolunteerItemClickListener {
                    override fun onVolunteerCardClick(volunteer: VolunteerDTO) {}
                },
                onBidBtnClick = object : VolunteerAndBidAdapter.OnBidBtnClickListener {
                    override fun onConfirmClick(bidItem: VolunteersBid) {
                    }

                    override fun onRejectClick(bidItem: VolunteersBid) {
                    }
                },
                imageLoader = imageLoader,
                userPropertiesRepository = userPropertiesRepository
            )
            adapter = volunteerAndBidAdapter
            layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false
            )
            val divider = DividerItemDecoration(
                requireContext(), LinearLayoutManager.VERTICAL
            )
            val drawable = AppCompatResources.getDrawable(
                requireContext(), R.drawable.volunteer_and_bid_divider
            )
            divider.setDrawable(drawable!!)
            addItemDecoration(divider)
        }

    }

    private fun showKennelName(kennelName: String) {
        val kennelNameResource = getString(R.string.volunteers_list_header_text, kennelName)
        binding.volunteersListHeader.text = kennelNameResource
    }
}