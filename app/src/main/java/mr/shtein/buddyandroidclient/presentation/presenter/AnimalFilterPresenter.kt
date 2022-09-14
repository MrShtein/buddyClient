package mr.shtein.buddyandroidclient.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter
import mr.shtein.buddyandroidclient.model.dto.AnimalFilter
import mr.shtein.buddyandroidclient.presentation.screen.AnimalFilterView

@InjectViewState
class AnimalFilterPresenter() : MvpPresenter<AnimalFilterView>() {

    private lateinit var animalFilter: AnimalFilter

    fun onInitView(animalFilter: AnimalFilter) {
        this.animalFilter = animalFilter
        viewState.setUpTransitions()
    }

}