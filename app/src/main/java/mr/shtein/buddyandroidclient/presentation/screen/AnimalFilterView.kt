package mr.shtein.buddyandroidclient.presentation.screen

import com.google.android.material.chip.Chip
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import mr.shtein.buddyandroidclient.model.dto.FilterAutocompleteItem

interface AnimalFilterView : MvpView {

    @AddToEndSingle
    fun setUpTransitions()

    @AddToEndSingle
    fun initAdapters(
        animalBreeds: List<FilterAutocompleteItem>,
        animalColors: List<FilterAutocompleteItem>,
        animalType: List<FilterAutocompleteItem>
    )

    @AddToEndSingle
    fun showBreedChips(breedsForChips: MutableList<FilterAutocompleteItem>)

    @AddToEndSingle
    fun showColorChips(colorsForChips: MutableList<FilterAutocompleteItem>)

    @AddToEndSingle
    fun showAnimalTypeChips(animalTypesForChips: MutableList<FilterAutocompleteItem>)

    @AddToEndSingle
    fun showCityChips(citiesForChips: MutableList<FilterAutocompleteItem>)

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
}
