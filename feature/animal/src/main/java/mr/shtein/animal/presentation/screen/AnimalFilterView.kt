package mr.shtein.animal.presentation.screen

import com.google.android.material.chip.Chip
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import mr.shtein.model.FilterAutocompleteItem

interface AnimalFilterView : MvpView {

    @AddToEndSingle
    fun setUpTransitions()

    @AddToEndSingle
    fun initAdapters(
        animalBreeds: List<FilterAutocompleteItem>,
        animalColors: List<FilterAutocompleteItem>,
        animalTypes: List<FilterAutocompleteItem>,
        animalCities: List<FilterAutocompleteItem>
    )

    @AddToEndSingle
    fun createBreedChip(filterBreed: FilterAutocompleteItem)

    @AddToEndSingle
    fun createColorChip(filterColor: FilterAutocompleteItem)

    @AddToEndSingle
    fun createAnimalTypeChip(filterAnimalType: FilterAutocompleteItem)

    @AddToEndSingle
    fun createCityChip(filterCity: FilterAutocompleteItem)

    @AddToEndSingle
    fun showBreedChips(breedsForChips: MutableSet<FilterAutocompleteItem>)

    @AddToEndSingle
    fun showColorChips(colorsForChips: MutableSet<FilterAutocompleteItem>)

    @AddToEndSingle
    fun showAnimalTypeChips(animalTypesForChips: MutableSet<FilterAutocompleteItem>)

    @AddToEndSingle
    fun showCityChips(citiesForChips: MutableSet<FilterAutocompleteItem>)

    @OneExecution
    fun deleteBreedChip(chip: Chip)

    @AddToEndSingle
    fun updateBreedList(breeds: List<FilterAutocompleteItem>?)

    @AddToEndSingle
    fun setListeners()

    @AddToEndSingle
    fun updateBtnValue(animalAfterFilteredCount: Int)

    @OneExecution
    fun closeKeyboard()

    @OneExecution
    fun deleteColorChip(chip: Chip)

    @AddToEndSingle
    fun updateColorList(colors: List<FilterAutocompleteItem>?)

    @OneExecution
    fun deleteCityChip(chip: Chip)

    @AddToEndSingle
    fun updateCityList(cities: List<FilterAutocompleteItem>?)

    @OneExecution
    fun deleteAnimalTypeChip(chip: Chip)

    @AddToEndSingle
    fun updateAnimalTypeList(types: Set<FilterAutocompleteItem>?)

    @AddToEndSingle
    fun setMinMaxAge(minAge: Int, maxAge: Int)

    @AddToEndSingle
    fun updateMinMaxAge(minAge: Int, maxAge: Int)

    @AddToEndSingle
    fun showMaleGender()

    @AddToEndSingle
    fun showFemaleGender()

    @AddToEndSingle
    fun showAnyGender()

    @OneExecution
    fun showNetworkErrorMsg()

    @OneExecution
    fun showServerErrorMsg()

    @OneExecution
    fun showTokenErrorMsg()
}
