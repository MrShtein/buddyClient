package mr.shtein.kennel.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.kennel.R
import mr.shtein.kennel.databinding.VolunteersListFragmentBinding
import mr.shtein.kennel.presentation.viewmodel.VolunteerListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class VolunteersListFragment : Fragment(R.layout.volunteers_list_fragment) {

    private var _binding: VolunteersListFragmentBinding? = null
    private val binding
        get() = _binding
    private val volunteerViewModel: VolunteerListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        volunteerViewModel.onFragmentCreate("Три кита")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = VolunteersListFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }


}