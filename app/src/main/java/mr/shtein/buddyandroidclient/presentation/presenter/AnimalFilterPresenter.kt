package mr.shtein.buddyandroidclient.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractor
import mr.shtein.buddyandroidclient.model.dto.*
import mr.shtein.buddyandroidclient.presentation.screen.AnimalFilterView

@InjectViewState
class AnimalFilterPresenter(
    val animalInteractor: AnimalInteractor,

) : MvpPresenter<AnimalFilterView>() {

    private lateinit var animalFilter: AnimalFilter
    private lateinit var animalBreeds: List<FilterAutocompleteItem>
    private lateinit var animalColors: List<AnimalCharacteristic>
    private lateinit var animalType: List<AnimalType>

    fun onInitView(animalFilter: AnimalFilter) {
        this.animalFilter = animalFilter
        viewState.setUpView()
        CoroutineScope(Dispatchers.Main).launch {
            animalBreeds = animalInteractor.getAnimalBreeds(1)
            viewState.initAdapters(animalBreeds)

        }
    }

}