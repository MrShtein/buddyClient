package mr.shtein.buddyandroidclient.presentation.screen

import android.os.Bundle
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MultiAutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.transition.MaterialSharedAxis
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.SelectionAdapter
import mr.shtein.buddyandroidclient.databinding.AnimalFilterFragmentBinding
import mr.shtein.buddyandroidclient.model.dto.AnimalFilter
import mr.shtein.buddyandroidclient.model.dto.FilterAutocompleteItem
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalFilterPresenter
import mr.shtein.buddyandroidclient.utils.FragmentsListForAssigningAnimation
import org.koin.android.ext.android.get

private const val ANIMAL_FILTER_KEY = "animal_filter"
private const val LAST_FRAGMENT_KEY = "last_fragment"

class AnimalFilterFragment : MvpAppCompatFragment(), AnimalFilterView {

    private var _binding: AnimalFilterFragmentBinding? = null
    private lateinit var breedsAdapter: SelectionAdapter
    private val binding get () = _binding!!
    
    @InjectPresenter
    lateinit var animalFilterPresenter: AnimalFilterPresenter

    @ProvidePresenter
    fun providePresenter(): AnimalFilterPresenter {
        return get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AnimalFilterFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        initPresenter()
        binding.animalFilterFindBtn.setOnClickListener {
            val bundle = bundleOf(LAST_FRAGMENT_KEY to FragmentsListForAssigningAnimation.ANIMAL_FILTER)
            findNavController().navigate(R.id.action_animalFilterFragment_to_animalsListFragment, bundle)
        }
        return view
    }

    override fun setUpView() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun initAdapters(animalBreeds: List<FilterAutocompleteItem>) {
        breedsAdapter = SelectionAdapter(requireContext(), animalBreeds)
        binding.animalFilterBreedInput.setAdapter(breedsAdapter)
//        binding.animalFilterBreedInput.threshold = 1
        binding.animalFilterBreedInput.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        binding.animalFilterBreedInput.setOnItemClickListener { parent, view, position, id ->
            val filterBreed = breedsAdapter.getItem(position)
            filterBreed?.isSelected = true
            val chip = ChipDrawable.createFromResource(requireContext(), R.xml.filter_item_chip)
            chip.setBounds(0, 0, chip.intrinsicWidth, chip.intrinsicHeight)
            chip.text = filterBreed?.name
            val span = ImageSpan(chip)
            binding.animalFilterBreedInput.text.setSpan(span, 0, filterBreed?.name?.length!!, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun initPresenter() {
        val animalFilter = arguments?.getParcelable(ANIMAL_FILTER_KEY) ?: AnimalFilter()
        animalFilterPresenter.onInitView(animalFilter)
    }
}