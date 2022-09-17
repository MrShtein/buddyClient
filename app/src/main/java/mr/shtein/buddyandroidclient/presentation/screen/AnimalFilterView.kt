package mr.shtein.buddyandroidclient.presentation.screen

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import mr.shtein.buddyandroidclient.model.dto.FilterAutocompleteItem

interface AnimalFilterView : MvpView {

    @AddToEndSingle
    fun setUpView()
    @AddToEndSingle
    fun initAdapters(animalBreeds: List<FilterAutocompleteItem>)
}
