package mr.shtein.kennel.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mr.shtein.data.model.KennelPreview
import mr.shtein.kennel.R
import mr.shtein.kennel.databinding.VolunteersListFragmentBinding
import mr.shtein.kennel.presentation.viewmodel.VolunteerListViewModel
import mr.shtein.ui_util.setInsetsListenerForPadding
import mr.shtein.ui_util.setStatusBarColor
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
        setInsetsListenerForPadding(
            binding.volunteersListHeader,
            left = false,
            top = true,
            right = false,
            bottom = false
        )
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                volunteerViewModel.volunteerListState.collect {
                    showKennelName(it.kennelName)
                }
            }
        }
    }

    private fun showKennelName(kennelName: String) {
        val kennelNameResource = getString(R.string.volunteers_list_header_text, kennelName)
        binding?.volunteersListHeader?.text = kennelNameResource
    }
}