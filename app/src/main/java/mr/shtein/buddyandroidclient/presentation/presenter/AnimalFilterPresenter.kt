package mr.shtein.buddyandroidclient.presentation.presenter

import com.google.android.material.chip.Chip
import kotlinx.coroutines.*
import moxy.InjectViewState
import moxy.MvpPresenter
import mr.shtein.buddyandroidclient.data.repository.CityRepository
import mr.shtein.buddyandroidclient.domain.interactor.AnimalFilterInteractor
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractor
import mr.shtein.buddyandroidclient.model.dto.*
import mr.shtein.buddyandroidclient.presentation.screen.AnimalFilterView

private const val DOG_ID = 1
private const val CAT_ID = 2
private const val COLOR_ID = 1

@InjectViewState
class AnimalFilterPresenter(
    private val animalInteractor: AnimalInteractor,
    private val cityRepository: CityRepository,
    private val animalFilterInteractor: AnimalFilterInteractor,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Main

) : MvpPresenter<AnimalFilterView>() {

    private var scope: CoroutineScope = CoroutineScope(coroutineDispatcher)
    private lateinit var animalFilter: AnimalFilter
    private var cities: List<FilterAutocompleteItem>? = null
    private var breeds: List<FilterAutocompleteItem>? = null
    private var colors: List<FilterAutocompleteItem>? = null
    private var types: List<FilterAutocompleteItem>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        animalFilter = animalFilterInteractor.makeAnimalFilter()
        setUpView()
    }

    private fun setUpView() {
        viewState.setUpTransitions()
        scope.launch {
            val cityJob = launch {
                val citiesForChips: MutableList<FilterAutocompleteItem> = mutableListOf()
                if (cities == null) {
                    val citiesList = cityRepository.getCities()
                    cities = mapCitiesToFilterItem(citiesList)
                    cities?.map { item ->
                        animalFilter.cityId?.forEach {
                            if (it == item.id) {
                                item.isSelected = true
                                citiesForChips.add(item)
                            }
                        }
                    }
                } else {
                    val citiesForChipList = cities!!
                        .filter {
                            it.isSelected
                        }
                    citiesForChips.addAll(citiesForChipList)
                }
                viewState.showCityChips(citiesForChips)
            }

            val breedJob = launch {
                val breedsForChips: MutableList<FilterAutocompleteItem> = mutableListOf()
                if (breeds == null) {
                    breeds =
                        animalInteractor.getAnimalBreeds(animalFilter.animalTypeId?.toList()!!)
                    breeds?.map { item ->
                        animalFilter.breedId?.forEach {
                            if (it == item.id) {
                                item.isSelected = true
                                breedsForChips.add(item)
                            }
                        }
                    }
                } else {
                    val breedsForChipsList = breeds!!
                        .filter {
                            it.isSelected
                        }
                    breedsForChips.addAll(breedsForChipsList)

                }
                viewState.showBreedChips(breedsForChips)
            }
            val colorJob = launch {
                val colorsForChips: MutableList<FilterAutocompleteItem> = mutableListOf()
                if (colors == null) {
                    colors = animalInteractor.getAnimalCharacteristics(COLOR_ID)
                    colors?.map { item ->
                        animalFilter.colorId?.forEach {
                            if (it == item.id) item.isSelected = true
                            colorsForChips.add(item)
                        }
                    }
                } else {
                    val colorsForChipsList = colors!!
                        .filter {
                            it.isSelected
                        }
                    colorsForChips.addAll(colorsForChipsList)
                }
                viewState.showColorChips(colorsForChips)
            }
            val typeJob = launch {
                val animalTypesForChips: MutableList<FilterAutocompleteItem> = mutableListOf()
                if (types == null) {
                    types = animalInteractor.getAnimalTypes()
                    types?.map { item ->
                        animalFilter.animalTypeId?.forEach {
                            if (it == item.id) item.isSelected = true
                            animalTypesForChips.add(item)
                        }
                    }
                } else {
                    val animalTypesForChipsList = types!!
                        .filter {
                            it.isSelected
                        }
                    animalTypesForChips.addAll(animalTypesForChipsList)
                }
                viewState.showAnimalTypeChips(animalTypesForChips)
            }
            launch {
                val animalCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
                viewState.updateBtnValue(animalCount)
            }
            breedJob.join()
            colorJob.join()
            typeJob.join()
            viewState.initAdapters(breeds!!, colors!!, types!!)
            viewState.setListeners()

        }
    }

    fun onBreedChipCloseBtnClicked(chip: Chip) {
        val breedId: Int = chip.tag as Int
        val breed = breeds?.first {
            breedId == it.id
        }
        breed?.isSelected = false
        animalFilter.breedId?.remove(breed?.id)
        scope.launch {
            val animalCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateBtnValue(animalCount)
            val storeBreedList = animalFilterInteractor.getBreedIdList()
            storeBreedList.remove(breed?.id)
            animalFilterInteractor.saveBreedIdList(storeBreedList)
        }
        viewState.deleteBreedChip(chip)
        viewState.updateBreedList(breeds)
        viewState.closeKeyboard()
    }


    fun onBreedFilterItemClick(filterBreed: FilterAutocompleteItem) {
        val storeBreedIdList = animalFilterInteractor.getBreedIdList()
        storeBreedIdList.add(filterBreed.id)
        animalFilterInteractor.saveBreedIdList(storeBreedIdList)
        if (animalFilter.breedId != null) {
            animalFilter.breedId!!.add(filterBreed.id)
        } else {
            animalFilter.breedId = mutableListOf(filterBreed.id)
        }
        scope.launch {
            val animalAfterFilteredCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateBtnValue(animalAfterFilteredCount)
        }
        viewState.closeKeyboard()
    }

    private fun mapCitiesToFilterItem(
        citiesList: MutableList<CityChoiceItem>
    ): List<FilterAutocompleteItem> {
        return citiesList
            .map {
                FilterAutocompleteItem(
                    id = it.city_id, name = "${it.name}, ${it.region}", isSelected = false
                )
            }
            .toList()
    }
}