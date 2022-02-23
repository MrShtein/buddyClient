package mr.shtein.buddyandroidclient.screens

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.CitiesAdapter
import mr.shtein.buddyandroidclient.adapters.OnCityListener
import mr.shtein.buddyandroidclient.model.dto.CityChoiceItem
import mr.shtein.buddyandroidclient.utils.CitiesGetHelper
import mr.shtein.buddyandroidclient.utils.CityCallback
import mr.shtein.buddyandroidclient.utils.SharedPreferences

private const val MAG = "City"
const val CITY_REQUEST_KEY = "new_city_request"
const val CITY_BUNDLE_KEY = "new_city_bundle"
const val IS_FROM_CITY_BUNDLE_KEY = "is_from_city_bundle"

class CityChoiceFragment : Fragment(R.layout.city_choice_fragment) {

    private lateinit var adapter: CitiesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var textHint: TextView
    private var citiesGetHelper = CitiesGetHelper(0)
    private var isCitiesVisible = false


    override fun onDestroy() {
        super.onDestroy()
        Log.d(MAG, "City Fragment destroyed")
    }

    override fun onPause() {
        super.onPause()
        Log.d(MAG, "City Fragment view PAUSED")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(MAG, "City Fragment Attache")
    }

    override fun onStop() {
        super.onStop()
        Log.d(MAG, "City Fragment view STOPPED")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(MAG, "City Fragment view created")
        textHint = view.findViewById(R.id.city_choice_description_text)

        val navController = findNavController()
        val navLabel = navController.previousBackStackEntry?.destination?.label

        if (navLabel == "UserSettingsFragment") {
            textHint.setText(R.string.city_choice_description_text_else_from_settings)
        }

        adapter = CitiesAdapter(citiesGetHelper.getCities(), object : OnCityListener {
            override fun onCityClick(cityItem: CityChoiceItem) {
                val sharedPropertyWriter =
                    SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
                val cityForStorage = makeStringForCityStorage(cityItem)
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
                    else -> {
                        navController.navigate(R.id.action_cityChoiceFragment_to_bottomNavFragment)
                    }
                }
            }
        })


        val cityInput = view.findViewById<TextInputEditText>(R.id.input_text_for_city_choice)

        cityInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textHint.isVisible = false
                recyclerView = view.findViewById(R.id.city_list)
                recyclerView.setPadding(0, 10, 0, 0)
                recyclerView.adapter = adapter
                recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            }
        }
        cityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isCitiesVisible) {
                    //animateCitiesAlpha(list)
                    isCitiesVisible = true
                }
                val newCities =
                    citiesGetHelper.onCitiesChanged(s.toString(), citiesGetHelper.cityChoiceItems)
                getNewCitiesWithDiffUtil(newCities)
            }

            override fun afterTextChanged(s: Editable?) {
                return
            }
        })
    }


    private fun getNewCitiesWithDiffUtil(newCityChoiceItemList: List<CityChoiceItem>) {
        val callback = CityCallback(adapter.cityChoiceItems, newCityChoiceItemList)
        val diff = DiffUtil.calculateDiff(callback)
        diff.dispatchUpdatesTo(adapter)
        if (citiesGetHelper.lettersCount == 1) {
            animateCitiesAlpha(recyclerView)
        }
        adapter.cityChoiceItems = newCityChoiceItemList
    }

    private fun makeStringForCityStorage(city: CityChoiceItem): String {
        return "${city.city_id},${city.name},${city.region}"
    }

    private fun animateCitiesAlpha(list: RecyclerView) {
        ObjectAnimator
            .ofFloat(list, "alpha", 1f).apply {
                duration = 250
            }
            .start()
    }
}