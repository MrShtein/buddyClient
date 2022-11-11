package mr.shtein.buddyandroidclient.presentation.presenter

import com.google.android.material.chip.Chip
import kotlinx.coroutines.*
import moxy.InjectViewState
import moxy.MvpPresenter
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.data.repository.CityRepository
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.domain.interactor.AnimalFilterInteractor
import mr.shtein.buddyandroidclient.domain.interactor.AnimalInteractor
import mr.shtein.buddyandroidclient.exceptions.validate.BadTokenException
import mr.shtein.buddyandroidclient.exceptions.validate.ServerErrorException
import mr.shtein.buddyandroidclient.model.dto.*
import mr.shtein.buddyandroidclient.presentation.screen.AnimalFilterView
import mr.shtein.model.CityChoiceItem
import mr.shtein.model.FilterAutocompleteItem
import java.net.ConnectException
import java.net.SocketTimeoutException

private const val MALE_ID = 1
private const val FEMALE_ID = 2
private const val COLOR_ID = 1
private const val MIN_AGE_VALUE = 0
private const val MAX_AGE_VALUE = 35
private const val EMPTY_VALUE = -1
private const val MONTHS_IN_YEAR = 12

@InjectViewState
class AnimalFilterPresenter(
    private val animalInteractor: AnimalInteractor,
    private val cityRepository: CityRepository,
    private val animalFilterInteractor: AnimalFilterInteractor,
    private val userPropertiesRepository: UserPropertiesRepository,
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
        viewState.setListeners()
        val handler = CoroutineExceptionHandler { _, exception ->
            when (exception) {
                is SocketTimeoutException -> {
                    viewState.showNetworkErrorMsg()
                }
                is ConnectException -> {
                    viewState.showNetworkErrorMsg()
                }
                is ServerErrorException -> {
                    viewState.showServerErrorMsg()
                }
                is BadTokenException -> {
                    resetToken()
                    viewState.showTokenErrorMsg()
                }
                else -> {}
            }
        }
        scope.launch(handler) {
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
                        animalInteractor.getAnimalBreeds(animalFilter.animalTypeId!!)
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

                val minAge = animalFilterInteractor.getMinAge()
                val maxAge = animalFilterInteractor.getMaxAge()
                if (minAge != EMPTY_VALUE && maxAge != EMPTY_VALUE) {
                    val minAgeInMonth = minAge / MONTHS_IN_YEAR
                    val maxAgeInMonth = maxAge / MONTHS_IN_YEAR
                    viewState.setMinMaxAge(minAge = minAgeInMonth, maxAge = maxAgeInMonth)
                } else {
                    viewState.setMinMaxAge(minAge = MIN_AGE_VALUE, maxAge = MAX_AGE_VALUE)
                }

                when (animalFilterInteractor.getGenderId()) {
                    MALE_ID -> {
                        viewState.showMaleGender()
                    }
                    FEMALE_ID -> {
                        viewState.showFemaleGender()
                    }
                    else -> {
                        viewState.showAnyGender()
                    }
                }

            }
            cityJob.join()
            breedJob.join()
            colorJob.join()
            typeJob.join()
            viewState.initAdapters(breeds!!, colors!!, types!!, cities!!)
        }
    }

    private fun resetToken() {
        userPropertiesRepository.removeAll()
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

    fun onAnimalTypeFilterItemClick(filterAnimalType: FilterAutocompleteItem) {
        val storeAnimalTypeIdList = animalFilterInteractor.getAnimalTypeIdList()
        storeAnimalTypeIdList.add(filterAnimalType.id)
        typesForChips.add(filterAnimalType)
        animalFilterInteractor.saveAnimalTypeIdList(storeAnimalTypeIdList)
        if (animalFilter.animalTypeId != null) {
            animalFilter.animalTypeId?.add(filterAnimalType.id)
        } else {
            animalFilter.animalTypeId = mutableListOf(filterAnimalType.id)
        }
        scope.launch {
            val animalAfterFilteredCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateBtnValue(animalAfterFilteredCount)
        }
        viewState.closeKeyboard()
    }

    fun onAgeSliderValueChange(valueFrom: Int, valueTo: Int) {
        if (valueFrom == MIN_AGE_VALUE && valueTo == MAX_AGE_VALUE) {
            animalFilterInteractor.saveMinAge(EMPTY_VALUE)
            animalFilterInteractor.saveMaxAge(EMPTY_VALUE)
            animalFilter.minAge = EMPTY_VALUE
            animalFilter.maxAge = EMPTY_VALUE
        } else {
            val valueFromInMonths = valueFrom * MONTHS_IN_YEAR
            val valueToInMonths = valueTo * MONTHS_IN_YEAR
            animalFilterInteractor.saveMinAge(valueFromInMonths)
            animalFilterInteractor.saveMaxAge(valueToInMonths)
            animalFilter.minAge = valueFromInMonths
            animalFilter.maxAge = valueToInMonths
        }
        scope.launch {
            val animalAfterFilteredCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateMinMaxAge(minAge = valueFrom, maxAge = valueTo)
            viewState.updateBtnValue(animalAfterFilteredCount)
        }
    }

    fun onGenderChange(checkedId: Int) {
        when (checkedId) {
            R.id.animal_filter_male_button -> {
                animalFilter.genderId = MALE_ID
                animalFilterInteractor.saveGenderId(MALE_ID)
                viewState.showMaleGender()
            }
            R.id.animal_filter_female_button -> {
                animalFilter.genderId = FEMALE_ID
                animalFilterInteractor.saveGenderId(FEMALE_ID)
                viewState.showFemaleGender()
            }
            else -> {
                animalFilter.genderId = EMPTY_VALUE
                animalFilterInteractor.saveGenderId(EMPTY_VALUE)
                viewState.showAnyGender()
            }
        }
        scope.launch {
            val animalAfterFilteredCount = animalInteractor.getAnimalsCountByFilter(animalFilter)
            viewState.updateBtnValue(animalAfterFilteredCount)
        }
    }

    private fun mapCitiesToFilterItem(
        citiesList: MutableList<CityChoiceItem>
    ): List<FilterAutocompleteItem> {
        CoroutineExceptionHandler
        return citiesList
            .map {
                FilterAutocompleteItem(
                    id = it.city_id, name = "${it.name}, ${it.region}", isSelected = false
                )
            }
            .toList()
    }
}