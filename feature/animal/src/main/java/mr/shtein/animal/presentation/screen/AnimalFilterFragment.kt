package mr.shtein.animal.presentation.screen

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.toSpanned
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors
import com.google.android.material.slider.RangeSlider
import com.google.android.material.transition.MaterialSharedAxis
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import mr.shtein.animal.R
import mr.shtein.animal.databinding.AnimalFilterFragmentBinding
import mr.shtein.animal.navigation.AnimalNavigation
import mr.shtein.animal.presentation.adapter.SelectionAdapter
import mr.shtein.model.FilterAutocompleteItem
import mr.shtein.animal.presentation.presenter.AnimalFilterPresenter
import mr.shtein.ui_util.FragmentsListForAssigningAnimation
import mr.shtein.ui_util.setInsetsListenerForPadding
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class AnimalFilterFragment : MvpAppCompatFragment(), AnimalFilterView {

    private var _binding: AnimalFilterFragmentBinding? = null
    private lateinit var breedsAdapter: SelectionAdapter
    private lateinit var colorsAdapter: SelectionAdapter
    private lateinit var citiesAdapter: SelectionAdapter
    private lateinit var typesAdapter: SelectionAdapter
    private val binding get() = _binding!!
    private val navigator: AnimalNavigation by inject()

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
        setInsetsListenerForPadding(
            view = binding.animalFilterConstraintLayout,
            left = false,
            top = true,
            right = false,
            bottom = true
        )
        return binding.root
    }

    override fun setUpTransitions() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun initAdapters(
        animalBreeds: List<FilterAutocompleteItem>,
        animalColors: List<FilterAutocompleteItem>,
        animalTypes: List<FilterAutocompleteItem>,
        animalCities: List<FilterAutocompleteItem>
    ) {
        breedsAdapter = SelectionAdapter(requireContext(), animalBreeds)
        binding.animalFilterBreedInput.setAdapter(breedsAdapter)
//        binding.animalFilterBreedInput.setDropDownBackgroundResource(R.color.white)

        colorsAdapter = SelectionAdapter(requireContext(), animalColors)
        binding.animalFilterColorInput.setAdapter(colorsAdapter)
//        binding.animalFilterColorInput.setDropDownBackgroundResource(R.color.white)

        citiesAdapter = SelectionAdapter(requireContext(), animalCities)
        binding.animalFilterCityInput.setAdapter(citiesAdapter)
//        binding.animalFilterCityInput.setDropDownBackgroundResource(R.color.white)

        typesAdapter = SelectionAdapter(requireContext(), animalTypes)
        binding.animalFilterAnimalTypeInput.setAdapter(typesAdapter)
//        binding.animalFilterAnimalTypeInput.setDropDownBackgroundResource(R.color.white)

    }

    override fun closeKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun showBreedChips(breedsForChips: MutableSet<FilterAutocompleteItem>) {
        val breedChips: Set<Chip> = makeBreedChips(breedsForChips)
        breedChips.forEach {
            binding.animalFilterBreedChipsContainer.addView(it)
        }
    }


    override fun showColorChips(colorsForChips: MutableSet<FilterAutocompleteItem>) {
        val colorChips: Set<Chip> = makeColorChips(colorsForChips)
        colorChips.forEach {
            binding.animalFilterColorChipsContainer.addView(it)
        }
    }

    override fun showAnimalTypeChips(animalTypesForChips: MutableSet<FilterAutocompleteItem>) {
        val animalTypeChips: Set<Chip> = makeAnimalTypeChips(animalTypesForChips)
        animalTypeChips.forEach {
            binding.animalFilterAnimalTypeChipsContainer.addView(it)
        }
    }

    override fun showCityChips(citiesForChips: MutableSet<FilterAutocompleteItem>) {
        val citiesChips: Set<Chip> = makeCityChips(citiesForChips)
        citiesChips.forEach {
            binding.animalFilterCityChipsContainer.addView(it)
        }
    }

    override fun setMinMaxAge(minAge: Int, maxAge: Int) {
        val text = makeStringForAgeLabel(minAge = minAge, maxAge = maxAge)
        binding.animalFilterAgeLabel.text = text
        binding.animalFilterAgeSlider.values = listOf(minAge.toFloat(), maxAge.toFloat())
    }

    override fun showMaleGender() {
        binding.animalFilterMaleButton.isChecked = true
    }

    override fun showFemaleGender() {
        binding.animalFilterFemaleButton.isChecked = true
    }

    override fun showAnyGender() {
        binding.animalFilterAnyGenderButton.isChecked = true
    }

    override fun updateMinMaxAge(minAge: Int, maxAge: Int) {
        val text = makeStringForAgeLabel(minAge = minAge, maxAge = maxAge)
        binding.animalFilterAgeLabel.text = text
    }

    override fun setListeners() {
        binding.animalFilterBreedInput.setOnItemClickListener { parent, view, position, id ->

            val adapter = binding.animalFilterBreedInput.adapter as SelectionAdapter
            val filterBreed = adapter.getItem(position)
            animalFilterPresenter.onBreedFilterItemClick(filterBreed!!)

            binding.animalFilterBreedInput.setText("")
        }

        binding.animalFilterColorInput.setOnItemClickListener { parent, view, position, id ->
            val adapter = binding.animalFilterColorInput.adapter as SelectionAdapter
            val filterColor = adapter.getItem(position)
            animalFilterPresenter.onColorFilterItemClick(filterColor!!)
            binding.animalFilterColorInput.setText("")
        }

        binding.animalFilterCityInput.setOnItemClickListener { parent, view, position, id ->
            val adapter = binding.animalFilterCityInput.adapter as SelectionAdapter
            val filterCity = adapter.getItem(position)
            animalFilterPresenter.onCityFilterItemClick(filterCity!!)
            binding.animalFilterCityInput.setText("")
        }

        binding.animalFilterAnimalTypeInput.setOnItemClickListener { parent, view, position, id ->
            val adapter = binding.animalFilterAnimalTypeInput.adapter as SelectionAdapter
            val filterAnimalType = adapter.getItem(position)
            animalFilterPresenter.onAnimalTypeFilterItemClick(filterAnimalType!!)
            binding.animalFilterAnimalTypeInput.setText("")
        }

        binding.animalFilterFindBtn.setOnClickListener {
            navigator.moveToAnimalListFromAnimalFilter(
                FragmentsListForAssigningAnimation.ANIMAL_FILTER
            )
        }

        binding.animalFilterAgeSlider.addOnSliderTouchListener(object :
            RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                val valueFrom = slider.values[0].toInt()
                val valueTo = slider.values[1].toInt()
                animalFilterPresenter.onAgeSliderValueChange(
                    valueFrom = valueFrom,
                    valueTo = valueTo
                )
            }
        })

        binding.animalFilterGenderGroup.setOnCheckedChangeListener { _, checkedId ->
            animalFilterPresenter.onGenderChange(checkedId)
        }
    }

    override fun updateBtnValue(animalAfterFilteredCount: Int) {
        binding.animalFilterFindBtn.text = resources.getQuantityString(
            R.plurals.filter_btn_animal_count,
            animalAfterFilteredCount,
            animalAfterFilteredCount
        )
    }

    override fun createBreedChip(filterBreed: FilterAutocompleteItem) {
        val chip = makeBreedChip(filterBreed.name, filterBreed.id)
        binding.animalFilterBreedChipsContainer.addView(chip)
    }

    override fun deleteBreedChip(chip: Chip) {
        binding.animalFilterBreedChipsContainer.removeView(chip)
    }

    override fun updateBreedList(breeds: List<FilterAutocompleteItem>?) {
        val adapter = binding.animalFilterBreedInput.adapter as SelectionAdapter
        adapter.clear()
        adapter.addAll(breeds!!)
        adapter.notifyDataSetChanged()
    }

    override fun createColorChip(filterColor: FilterAutocompleteItem) {
        val chip = makeColorChip(filterColor.name, filterColor.id)
        binding.animalFilterColorChipsContainer.addView(chip)
    }

    override fun deleteColorChip(chip: Chip) {
        binding.animalFilterColorChipsContainer.removeView(chip)
    }

    override fun updateColorList(colors: List<FilterAutocompleteItem>?) {
        val adapter = binding.animalFilterColorInput.adapter as SelectionAdapter
        adapter.clear()
        adapter.addAll(colors!!)
        adapter.notifyDataSetChanged()
    }

    override fun createCityChip(filterCity: FilterAutocompleteItem) {
        val chip = makeCityChip(filterCity.name, filterCity.id)
        binding.animalFilterCityChipsContainer.addView(chip)
    }

    override fun deleteCityChip(chip: Chip) {
        binding.animalFilterCityChipsContainer.removeView(chip)
    }

    override fun updateCityList(cities: List<FilterAutocompleteItem>?) {
        val adapter = binding.animalFilterCityInput.adapter as SelectionAdapter
        adapter.clear()
        adapter.addAll(cities!!)
        adapter.notifyDataSetChanged()
    }

    override fun createAnimalTypeChip(filterAnimalType: FilterAutocompleteItem) {
        val chip = makeAnimalTypeChip(filterAnimalType.name, filterAnimalType.id)
        binding.animalFilterAnimalTypeChipsContainer.addView(chip)
    }

    override fun deleteAnimalTypeChip(chip: Chip) {
        binding.animalFilterAnimalTypeChipsContainer.removeView(chip)
    }

    override fun updateAnimalTypeList(types: Set<FilterAutocompleteItem>?) {
        val adapter = binding.animalFilterAnimalTypeInput.adapter as SelectionAdapter
        adapter.clear()
        adapter.addAll(types!!)
        adapter.notifyDataSetChanged()
    }

    override fun showNetworkErrorMsg() {
        val networkErrorMsg = getString(R.string.internet_failure_text)
        Toast.makeText(requireContext(), networkErrorMsg, Toast.LENGTH_LONG).show()
    }

    override fun showServerErrorMsg() {
        val serverErrorMsg = getString(R.string.server_unavailable_msg)
        Toast.makeText(requireContext(), serverErrorMsg, Toast.LENGTH_LONG).show()
    }

    override fun showTokenErrorMsg() {
        val tokenErrorMsg = getString(R.string.bad_token_msg)
        Toast.makeText(requireContext(), tokenErrorMsg, Toast.LENGTH_LONG).show()
    }

    private fun makeBreedChips(breedsForChips: MutableSet<FilterAutocompleteItem>): Set<Chip> {
        return breedsForChips
            .map {
                makeBreedChip(it.name, it.id)
            }
            .toSet()
    }

    private fun makeColorChips(colorsForChips: MutableSet<FilterAutocompleteItem>): Set<Chip> {
        return colorsForChips
            .map {
                makeColorChip(it.name, it.id)
            }
            .toSet()
    }

    private fun makeCityChips(citiesForChips: MutableSet<FilterAutocompleteItem>): Set<Chip> {
        return citiesForChips
            .map {
                makeCityChip(it.name, it.id)
            }
            .toSet()
    }

    private fun makeAnimalTypeChips(animalTypesForChips: MutableSet<FilterAutocompleteItem>): Set<Chip> {
        return animalTypesForChips
            .map {
                makeAnimalTypeChip(it.name, it.id)
            }
            .toSet()
    }

    private fun makeBreedChip(text: String, itemId: Int): Chip {
        val chip = layoutInflater.inflate(
            R.layout.filter_item_chip,
            binding.animalFilterBreedChipsContainer,
            false
        ) as Chip
        chip.text = text
        chip.tag = itemId
        chip.setOnClickListener {
            animalFilterPresenter.onBreedChipCloseBtnClicked(it as Chip)
        }
        return chip
    }

    private fun makeColorChip(text: String, itemId: Int): Chip {
        val chip = layoutInflater.inflate(
            R.layout.filter_item_chip,
            binding.animalFilterColorChipsContainer,
            false
        ) as Chip
        chip.text = text
        chip.tag = itemId
        chip.setOnClickListener {
            animalFilterPresenter.onColorChipCloseBtnClicked(it as Chip)
        }
        return chip
    }

    private fun makeCityChip(text: String, itemId: Int): Chip {
        val chip = layoutInflater.inflate(
            R.layout.filter_item_chip,
            binding.animalFilterCityChipsContainer,
            false
        ) as Chip
        chip.text = text
        chip.tag = itemId
        chip.setOnClickListener {
            animalFilterPresenter.onCityChipCloseBtnClicked(it as Chip)
        }
        return chip
    }

    private fun makeAnimalTypeChip(text: String, itemId: Int): Chip {
        val chip = layoutInflater.inflate(
            R.layout.filter_item_chip,
            binding.animalFilterAnimalTypeChipsContainer,
            false
        ) as Chip
        chip.text = text
        chip.tag = itemId
        chip.setOnClickListener {
            animalFilterPresenter.onAnimalTypeChipCloseBtnClicked(it as Chip)
        }
        return chip
    }

    private fun makeStringForAgeLabel(minAge: Int, maxAge: Int): Spanned {
        val ageText = getString(R.string.age_text)
        val fromText = getString(R.string.from_text)
        val untilText = getString(R.string.until_text)
        val minMaxAgeNumColor = MaterialColors.getColor(requireContext(), R.attr.colorPrimary,R.color.black)
        val spannableBuilder = SpannableStringBuilder()
        spannableBuilder
            .bold { append(ageText) }
            .append("    ")
            .append(fromText)
            .append("  ")
            .color(minMaxAgeNumColor) { append(minAge.toString()) }
            .append("  ")
            .append("лет    ")
            .append(untilText)
            .append("  ")
            .color(minMaxAgeNumColor) { append(maxAge.toString()) }
            .append("  лет")
        return spannableBuilder.toSpanned()
    }

}