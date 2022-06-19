package mr.shtein.buddyandroidclient.screens

import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.db.CityDbHelper
import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem
import mr.shtein.buddyandroidclient.setInsetsListenerForPadding
import mr.shtein.buddyandroidclient.setStatusBarColor
import mr.shtein.buddyandroidclient.utils.CityArrayAdapter
import mr.shtein.buddyandroidclient.utils.SharedPreferences

private const val MAG = "City"
const val CITY_REQUEST_KEY = "new_city_request"
const val CITY_BUNDLE_KEY = "new_city_bundle"
const val IS_FROM_CITY_BUNDLE_KEY = "is_from_city_bundle"

class CityChoiceFragment : Fragment(R.layout.city_choice_fragment) {

    private lateinit var adapter: ArrayAdapter<CityChoiceItem>
    private lateinit var cityInputText: AutoCompleteTextView
    private lateinit var cityInputContainer: TextInputLayout
    private lateinit var textHint: TextView
    private val coroutine = CoroutineScope(Dispatchers.Main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setListeners()
        coroutine.launch {
            val cities = getCitiesFromDb()
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
            val navController = findNavController()
            val navLabel = navController.previousBackStackEntry?.destination?.label

            if (navLabel == "UserSettingsFragment" || navLabel == "UserProfileFragment") {
                textHint.setText(R.string.city_choice_description_text_else_from_settings)
            }
            val cityChoiceItem = adapter.getItemAtPosition(position) as CityChoiceItem
            val sharedPropertyWriter =
                SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
            val cityForStorage = makeStringForCityStorage(cityChoiceItem)
            sharedPropertyWriter.writeString(SharedPreferences.USER_CITY_KEY, cityForStorage)

            when (navLabel) {
                "UserSettingsFragment" -> {
                    setFragmentResult(
                        CITY_REQUEST_KEY, bundleOf(
                            CITY_BUNDLE_KEY to cityForStorage,
                            IS_FROM_CITY_BUNDLE_KEY to true
                        )
                    )
                    navController.popBackStack()
                }
                "KennelSettingsFragment" -> {
                    setFragmentResult(
                        CITY_REQUEST_KEY, bundleOf(
                            CITY_BUNDLE_KEY to cityForStorage,
                            IS_FROM_CITY_BUNDLE_KEY to true
                        )
                    )
                    navController.popBackStack()
                }
                "UserProfileFragment" -> {

                    navController.popBackStack(
                        R.id.animalsListFragment,
                        false
                    )
                }
                else -> {
                    navController.navigate(R.id.action_cityChoiceFragment_to_bottomNavFragment)
                }
            }
        }

        cityInputText.setOnFocusChangeListener { inputContainer, hasFocus ->
            if (hasFocus) {
                changeViewsStyle()
            }
        }
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
        val citiesList = getCitiesFromDb()
    }

    private suspend fun getCitiesFromDb(): MutableList<CityChoiceItem> =
        withContext(Dispatchers.IO) {
            val context = requireContext()
            val db = CityDbHelper(context).readableDatabase
            val storage = SharedPreferences(context, SharedPreferences.PERSISTENT_STORAGE_NAME)
            val cursor = db.query(
                storage.readString(SharedPreferences.DATABASE_NAME, ""),
                null, null, null,
                null, null, null, null
            )
            return@withContext makeCityChoiceItemsFromCursor(cursor)
        }

    private fun makeCityChoiceItemsFromCursor(cursor: Cursor): MutableList<CityChoiceItem> {
        val cityChoiceItemsList = arrayListOf<CityChoiceItem>()
        with(cursor) {
            while (moveToNext()) {
                val cityChoiceItem = CityChoiceItem(
                    getInt(getColumnIndexOrThrow("id")),
                    getString(getColumnIndexOrThrow("city_name")),
                    getString(getColumnIndexOrThrow("region_name"))
                )
                cityChoiceItemsList.add(cityChoiceItem)
            }
        }
        return cityChoiceItemsList
    }

    private fun makeStringForCityStorage(city: CityChoiceItem): String {
        return "${city.city_id},${city.name},${city.region}"
    }

}