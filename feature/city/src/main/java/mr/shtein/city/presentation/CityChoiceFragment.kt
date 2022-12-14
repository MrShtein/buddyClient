package mr.shtein.city.presentation

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.transition.Slide
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.shtein.city.R
import mr.shtein.city.navigation.CityNavigation
import mr.shtein.city.adapter.CityArrayAdapter
import mr.shtein.city.data.CityDbHelper
import mr.shtein.data.repository.CityRepository
import mr.shtein.city.data.DatabasePropertiesRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.model.CityChoiceItem
import mr.shtein.ui_util.setInsetsListenerForPadding
import mr.shtein.ui_util.setStatusBarColor
import org.koin.android.ext.android.inject

const val CITY_REQUEST_KEY = "new_city_request"
const val CITY_BUNDLE_KEY = "new_city_bundle"
const val IS_FROM_CITY_BUNDLE_KEY = "is_from_city_bundle"


class CityChoiceFragment : Fragment(R.layout.city_choice_fragment) {

    private lateinit var adapter: ArrayAdapter<CityChoiceItem>
    private lateinit var cityInputText: AutoCompleteTextView
    private lateinit var cityInputContainer: TextInputLayout
    private lateinit var textHint: TextView
    private val coroutine = CoroutineScope(Dispatchers.Main)
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val databasePropertiesRepository: DatabasePropertiesRepository by inject()
    private val cityDbHelper: CityDbHelper by inject()
    private val localDbCityRepository: CityRepository by inject()
    private val navigator: CityNavigation by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val previousFragment = navigator.getPreviousBackStackEntry()
        if (previousFragment == null) {
            val slideEnterTransition = Slide()
            slideEnterTransition.duration = 300
            slideEnterTransition.slideEdge = Gravity.RIGHT
            slideEnterTransition.interpolator = DecelerateInterpolator()
            enterTransition = slideEnterTransition

        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setListeners()
        coroutine.launch {
            val cities = localDbCityRepository.getCities()
            adapter = CityArrayAdapter(requireContext(), cities)
            cityInputText.setAdapter(adapter)
        }
        setStatusBarColor(true)
        setInsetsListenerForPadding(view, left = false, top = true, right = false, bottom = false)
    }

    private fun initViews(view: View) {
        cityInputText = view.findViewById(R.id.input_text_for_city_choice)
        cityInputContainer = view.findViewById(R.id.input_text_for_city_choice_container)
        textHint = view.findViewById(R.id.city_choice_description_text)
    }

    private fun setListeners() {
        cityInputText.setOnItemClickListener { adapter, _, position, _ ->
            val navLabel = navigator.getPreviousNavLabel()

            if (navLabel == "UserSettingsFragment" || navLabel == "UserProfileFragment") {
                textHint.setText(R.string.city_choice_description_text_else_from_settings)
            }
            val cityChoiceItem = adapter.getItemAtPosition(position) as CityChoiceItem
            val cityForStorage = makeStringForCityStorage(cityChoiceItem)
            userPropertiesRepository.saveUserCity(cityForStorage)

            when (navLabel) {
                "UserSettingsFragment" -> {
                    setFragmentResult(
                        CITY_REQUEST_KEY, bundleOf(
                            CITY_BUNDLE_KEY to cityForStorage,
                            IS_FROM_CITY_BUNDLE_KEY to true
                        )
                    )
                    hideKeyboard()
                    navigator.popBackStack()
                }
                "KennelSettingsFragment" -> {
                    setFragmentResult(
                        CITY_REQUEST_KEY, bundleOf(
                            CITY_BUNDLE_KEY to cityForStorage,
                            IS_FROM_CITY_BUNDLE_KEY to true
                        )
                    )
                    hideKeyboard()
                    navigator.popBackStack()
                }
                "UserProfileFragment" -> {
                    hideKeyboard()
                    navigator.backToAnimalList()
                }
                else -> {
                    hideKeyboard()
                    navigator.moveToAnimalListFromCity()

                }
            }
        }

        cityInputText.setOnFocusChangeListener { inputContainer, hasFocus ->
            if (hasFocus) {
                changeViewsStyle()
            }
        }
    }

    private fun hideKeyboard() {
        val inputManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun changeViewsStyle() {
        val inputContainerTopStartCorner = cityInputContainer.boxCornerRadiusTopStart
        val inputContainerTopEndCorner = cityInputContainer.boxCornerRadiusTopEnd
        val inputContainerBottomStartCorner = 0f
        val inputContainerBottomEndCorner = 0f
        cityInputContainer.setBoxCornerRadii(
            inputContainerTopStartCorner, inputContainerTopEndCorner,
            inputContainerBottomStartCorner, inputContainerBottomEndCorner
        )
    }

    private suspend fun makeCityArrayAdapter() {
        val citiesList = localDbCityRepository.getCities()
    }

    private fun makeStringForCityStorage(city: CityChoiceItem): String {
        return "${city.city_id},${city.name},${city.region}"
    }

}