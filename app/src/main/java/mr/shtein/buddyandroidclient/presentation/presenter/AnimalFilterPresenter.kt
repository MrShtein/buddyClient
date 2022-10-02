package mr.shtein.buddyandroidclient.presentation.presenter

import android.util.Log
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
    private lateinit var colorsForChips: MutableList<FilterAutocompleteItem>
    private lateinit var breedsForChips: MutableList<FilterAutocompleteItem>
    private lateinit var citiesForChips: MutableList<FilterAutocompleteItem>
    private lateinit var typesForChips: MutableList<FilterAutocompleteItem>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        animalFilter = animalFilterInteractor.makeAnimalFilter()
        initChipItemsContainers()
        setUpView()
    }

    private fun initChipItemsContainers() {
        colorsForChips = mutableListOf()
        breedsForChips = mutableListOf()
        citiesForChips = mutableListOf()
        typesForChips = mutableListOf()
    }

    private fun setUpView() {
        viewState.setUpTransitions()
        scope.launch {
            val cityJob = launch {
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
                if (colors == null) {
                    colors = animalInteractor.getAnimalCharacteristics(COLOR_ID)
                    colors?.map { item ->
                        animalFilter.colorId?.forEach {
                            if (it == item.id) {
                                item.isSelected = true
                                colorsForChips.add(item)
                            }
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
                            if (it == item.id) {
                                item.isSelected = true
                                animalTypesForChips.add(item)
                            }
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
            cityJob.join()
            breedJob.join()
            colorJob.join()
            typeJob.join()
            viewState.initAdapters(breeds!!, colors!!, types!!, cities!!)
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
        breedsForChips.remove(breed)
        scope.launch {
            val animalCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateBtnValue(animalCount)
            val storeBreedList = animalFilterInteractor.getBreedIdList()
            storeBreedList.remove(breed?.id)
            animalFilterInteractor.saveBreedIdList(storeBreedList)
        }
        viewState.deleteBreedChip(chip)
        viewState.updateBreedList(breeds?.toList())
        viewState.closeKeyboard()
    }

    fun onColorChipCloseBtnClicked(chip: Chip) {
        val colorId: Int = chip.tag as Int
        val color = colors?.first {
            colorId == it.id
        }
        color?.isSelected = false
        animalFilter.colorId?.remove(color?.id)
        colorsForChips.remove(color)
        scope.launch {
            val animalCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateBtnValue(animalCount)
            val storeColorList = animalFilterInteractor.getColorIdIdList()
            storeColorList.remove(color?.id)
            animalFilterInteractor.saveColorIdList(storeColorList)
        }
        viewState.deleteColorChip(chip)
        viewState.updateColorList(colors?.toList())
        viewState.closeKeyboard()
    }

    fun onCityChipCloseBtnClicked(chip: Chip) {
        val cityId: Int = chip.tag as Int
        val city = cities?.first {
            cityId == it.id
        }
        city?.isSelected = false
        animalFilter.cityId?.remove(city?.id)
        citiesForChips.remove(city)
        scope.launch {
            val animalCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateBtnValue(animalCount)
            val storeCityList = animalFilterInteractor.getCityIdList()
            storeCityList.remove(city?.id)
            animalFilterInteractor.saveCityIdList(storeCityList)
        }
        viewState.deleteCityChip(chip)
        viewState.updateCityList(cities?.toList())
        viewState.closeKeyboard()
    }

    fun onAnimalTypeChipCloseBtnClicked(chip: Chip) {
        val animalTypeId: Int = chip.tag as Int
        val animalType = types?.first {
            animalTypeId == it.id
        }
        animalType?.isSelected = false
        animalFilter.animalTypeId?.remove(animalType?.id)
        typesForChips.remove(animalType)
        scope.launch {
            val animalCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateBtnValue(animalCount)
            val storeAnimalTypeList = animalFilterInteractor.getAnimalTypeIdList()
            storeAnimalTypeList.remove(animalType?.id)
            animalFilterInteractor.saveAnimalTypeIdList(storeAnimalTypeList)
        }
        viewState.deleteAnimalTypeChip(chip)
        viewState.updateAnimalTypeList(types?.toList())
        viewState.closeKeyboard()
    }

    fun onBreedFilterItemClick(filterBreed: FilterAutocompleteItem) {
        val storeBreedIdList = animalFilterInteractor.getBreedIdList()
        storeBreedIdList.add(filterBreed.id)
        breedsForChips.add(filterBreed)
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

    fun onColorFilterItemClick(filterColor: FilterAutocompleteItem) {
        val storeColorIdList = animalFilterInteractor.getColorIdIdList()
        storeColorIdList.add(filterColor.id)
        colorsForChips.add(filterColor)
        animalFilterInteractor.saveColorIdList(storeColorIdList)
        if (animalFilter.colorId != null) {
            animalFilter.colorId?.add(filterColor.id)
        } else {
            animalFilter.colorId = mutableListOf(filterColor.id)
        }
        scope.launch {
            val animalAfterFilteredCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateBtnValue(animalAfterFilteredCount)
        }
        viewState.closeKeyboard()
    }

    fun onCityFilterItemClick(filterCity: FilterAutocompleteItem) {
        val storeCityIdList = animalFilterInteractor.getCityIdList()
        storeCityIdList.add(filterCity.id)
        citiesForChips.add(filterCity)
        animalFilterInteractor.saveCityIdList(storeCityIdList)
        if (animalFilter.cityId != null) {
            animalFilter.cityId?.add(filterCity.id)
        } else {
            animalFilter.cityId = mutableListOf(filterCity.id)
        }
        scope.launch {
            val animalAfterFilteredCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateBtnValue(animalAfterFilteredCount)
        }
        viewState.closeKeyboard()
    }

    fun onAnimalTypeFilterItemClick(filterCity: FilterAutocompleteItem) {
        val storeAnimalTypeIdList = animalFilterInteractor.getAnimalTypeIdList()
        storeAnimalTypeIdList.add(filterCity.id)
        typesForChips.add(filterCity)
        animalFilterInteractor.saveAnimalTypeIdList(storeAnimalTypeIdList)
        if (animalFilter.animalTypeId != null) {
            animalFilter.animalTypeId?.add(filterCity.id)
        } else {
            animalFilter.animalTypeId = mutableListOf(filterCity.id)
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