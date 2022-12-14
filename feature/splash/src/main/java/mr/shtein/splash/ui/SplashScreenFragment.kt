package mr.shtein.splash.ui

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
import androidx.transition.Slide
import mr.shtein.data.model.AnimalFilter
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.domain.interactor.AnimalFilterInteractor
import mr.shtein.splash.R
import mr.shtein.splash.navigation.StartNavigation
import org.koin.android.ext.android.inject
import mr.shtein.ui_util.setStatusBarColor

const val ANIMAL_FILTER_KEY = "animal_filter"
private const val DOG_TYPE_KEY = 1
private const val CAT_TYPE_KEY = 2


class SplashScreenFragment : Fragment(R.layout.start_fragment) {

    private var isInsetsWorked = false
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val animalFilterInteractor: AnimalFilterInteractor by inject()
    private val navigator: StartNavigation by inject()
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
            val animalTypeFilter = if (city == "") {
                navigator.moveToCityChoiceFromSplashScreen()
            } else {
                if (animalFilter.animalTypeId == null) {
                    val animalTypes = mutableListOf(DOG_TYPE_KEY, CAT_TYPE_KEY)
                    animalFilterInteractor.saveAnimalTypeIdList(animalTypes)
                    animalFilter.animalTypeId = animalTypes
                }
                val animalFilter = bundleOf(ANIMAL_FILTER_KEY to animalFilter)
                navigator.moveToAnimalListFromSplash(animalFilter = animalFilter)
            }
        }, 3000)

        return view
    }
}