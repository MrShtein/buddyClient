package mr.shtein.buddyandroidclient.screens

import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.data.repository.FilterPropertiesRepository
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.domain.interactor.AnimalFilterInteractor
import mr.shtein.buddyandroidclient.model.dto.AnimalFilter
import mr.shtein.buddyandroidclient.setStatusBarColor
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

const val ANIMAL_FILTER_KEY = "animal_filter"

class SplashScreenFragment : Fragment(R.layout.start_fragment) {

    private var isInsetsWorked = false
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val animalFilterInteractor: AnimalFilterInteractor by inject()
    private lateinit var animalFilter: AnimalFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        animalFilter = animalFilterInteractor.makeAnimalFilter()
        val exitSlide = Slide()
        exitSlide.slideEdge = Gravity.LEFT
        exitSlide.duration = 300
        exitSlide.interpolator = DecelerateInterpolator()
        exitTransition = exitSlide
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        exitTransition
        setStatusBarColor(false)
        val view = super.onCreateView(inflater, container, savedInstanceState)

        when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                view?.setBackgroundColor(requireContext().getColor(R.color.cian5_10))
            }
        }
        val logoText: ImageView = view!!.findViewById(R.id.buddy_logo_text)
        ViewCompat.setOnApplyWindowInsetsListener(logoText) { textView, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val oldMargin = logoText.marginBottom
            textView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                if (!isInsetsWorked) bottomMargin = insets.bottom + oldMargin
                isInsetsWorked = true
            }
            WindowInsetsCompat.CONSUMED
        }
        view.postDelayed({
            isInsetsWorked = false

            val city = userPropertiesRepository.getUserCity()
            if (city == "") {
                findNavController().navigate(R.id.action_startFragment_to_cityChoiceFragment)
            } else {
                val bundle = bundleOf(ANIMAL_FILTER_KEY to animalFilter)
                findNavController().navigate(R.id.action_startFragment_to_animalsListFragment, bundle)
            }
        }, 3000)

        return view
    }
}