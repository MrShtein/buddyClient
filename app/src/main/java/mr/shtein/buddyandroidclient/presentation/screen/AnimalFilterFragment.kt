package mr.shtein.buddyandroidclient.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.databinding.AnimalFilterFragmentBinding
import mr.shtein.buddyandroidclient.model.dto.AnimalFilter
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalFilterPresenter
import mr.shtein.buddyandroidclient.utils.FragmentsListForAssigningAnimation
import org.koin.android.ext.android.get

private const val ANIMAL_FILTER_KEY = "animal_filter"
private const val LAST_FRAGMENT_KEY = "last_fragment"

class AnimalFilterFragment : MvpAppCompatFragment(), AnimalFilterView {

    private var _binding: AnimalFilterFragmentBinding? = null
    private val binding get () = _binding!!
    
    @InjectPresenter
    lateinit var animalFilterPresenter: AnimalFilterPresenter

    @ProvidePresenter
    fun providePresenter(): AnimalFilterPresenter {
        return get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
        
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AnimalFilterFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.animalFilterFindBtn.setOnClickListener {
            val bundle = bundleOf(LAST_FRAGMENT_KEY to FragmentsListForAssigningAnimation.ANIMAL_FILTER)
            findNavController().navigate(R.id.action_animalFilterFragment_to_animalsListFragment, bundle)
        }
        return view
    }

    override fun setUpTransitions() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    private fun initPresenter() {
        val animalFilter = arguments?.getParcelable(ANIMAL_FILTER_KEY) ?: AnimalFilter()
        animalFilterPresenter.onInitView(animalFilter)
    }
}