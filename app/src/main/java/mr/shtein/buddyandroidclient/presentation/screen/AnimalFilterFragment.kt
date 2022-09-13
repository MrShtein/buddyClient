package mr.shtein.buddyandroidclient.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.databinding.AnimalFilterFragmentBinding
import mr.shtein.buddyandroidclient.model.dto.AnimalFilter
import mr.shtein.buddyandroidclient.presentation.presenter.AnimalFilterPresenter
import org.koin.android.ext.android.get

private const val ANIMAL_FILTER_KEY = "animal_filter"

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
        return view
    }

    private fun initPresenter() {
        val animalFilter = arguments?.getParcelable(ANIMAL_FILTER_KEY) ?: AnimalFilter()
        animalFilterPresenter.onInitView(animalFilter)
    }
}