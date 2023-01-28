package mr.shtein.kennel.presentation

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.*
import mr.shtein.model.AnimalCharacteristic
import mr.shtein.model.Breed
import mr.shtein.model.AddOrUpdateAnimal
import mr.shtein.data.exception.BadTokenException
import mr.shtein.data.exception.EmptyBodyException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.Animal
import mr.shtein.data.model.Gender
import mr.shtein.data.model.ImageContainer
import mr.shtein.data.repository.AnimalBreedRepository
import mr.shtein.data.repository.AnimalCharacteristicsRepository
import mr.shtein.data.repository.AnimalRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.util.ImageValidator
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.setInsetsListenerForPadding
import mr.shtein.ui_util.setStatusBarColor
import mr.shtein.ui_util.showBadTokenDialog
import mr.shtein.util.ImageCompressor
import org.koin.android.ext.android.inject
import java.net.ConnectException
import java.net.SocketTimeoutException


private const val IMAGE_TYPE = "image/*"
private const val NO_PHOTO_ERROR = "Необходимо добавить хотя бы одну фотографию"
private const val NO_AGE_ERROR = "Необходимо указать возраст питомца"
private const val EMPTY_NAME_ERROR = "Необходимо ввести имя питомца"
private const val NOT_EXISTED_BREED_ERROR = "Необходимо выбрать породу из списка"
private const val EMPTY_COLOR_ERROR = "Необходимо выбрать цвет питомца"
private const val EMPTY_DESCRIPTION_ERROR = "Необходимо ввести описание питомца"
private const val KENNEL_ID_KEY = "kennel_id"
private const val SERVER_ERROR = "Что-то не так с сервером, попробуйте позже"
private const val ANIMAL_TYPE_ID_KEY = "animal_type_id"
private const val BUNDLE_KEY_FOR_ANIMAL_OBJECT = "animal_key"
private const val FROM_SETTINGS_FRAGMENT_KEY = "I'm from settings"
private const val FROM_ADD_ANIMAL_REQUEST_KEY = "from_add_animal_request_key"
private const val NEW_ANIMAL_ADDED_RESULT_KEY = "new_animal_added"
private const val ANIMAL_BUNDLE_KEY = "animal_bundle_key"


class AddAnimalFragment : Fragment(R.layout.add_animal_fragment) {

    private lateinit var scroll: NestedScrollView
    private lateinit var addBtn: Button
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var firstImage: ImageView
    private lateinit var firstImageAddBtn: ImageButton
    private lateinit var firstImageCancelBtn: ImageButton
    private lateinit var firstImageOverlay: View
    private lateinit var firstImageProgress: ProgressBar
    private lateinit var secondImage: ImageView
    private lateinit var secondImageAddBtn: ImageButton
    private lateinit var secondImageCancelBtn: ImageButton
    private lateinit var secondImageOverlay: View
    private lateinit var secondImageProgress: ProgressBar
    private lateinit var thirdImage: ImageView
    private lateinit var thirdImageAddBtn: ImageButton
    private lateinit var thirdImageCancelBtn: ImageButton
    private lateinit var thirdImageOverlay: View
    private lateinit var thirdImageProgress: ProgressBar
    private lateinit var fourthImage: ImageView
    private lateinit var fourthImageAddBtn: ImageButton
    private lateinit var fourthImageCancelBtn: ImageButton
    private lateinit var fourthImageOverlay: View
    private lateinit var fourthImageProgress: ProgressBar
    private lateinit var yearsSlider: Slider
    private lateinit var yearsContainer: View
    private lateinit var yearsNum: TextView
    private lateinit var yearsText: TextView
    private lateinit var monthsContainer: View
    private lateinit var monthsSlider: Slider
    private lateinit var monthsNum: TextView
    private lateinit var monthsText: TextView
    private lateinit var animalName: TextInputEditText
    private lateinit var animalNameInputContainer: TextInputLayout
    private lateinit var animalBreedsInputContainer: TextInputLayout
    private lateinit var animalBreedsInput: AutoCompleteTextView
    private lateinit var animalColorInputContainer: TextInputLayout
    private lateinit var animalDescriptionInputContainer: TextInputLayout
    private lateinit var animalColorInput: AutoCompleteTextView
    private lateinit var dialog: AlertDialog
    private lateinit var getSomeImages: ActivityResultLauncher<String>
    private lateinit var maleBtn: RadioButton
    private lateinit var femaleBtn: RadioButton
    private var isFromAnimalSettings: Boolean = false
    private var animalForChange: Animal? = null
    private val animalDto = AddOrUpdateAnimal()
    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var imageContainerList = arrayListOf<ImageContainer>()
    private var animalBreeds: List<Breed> = mutableListOf()
    private var animalColors: List<AnimalCharacteristic> = listOf()
    private var deletedImage: ImageContainer? = null
    private var isInsetsWorked = false
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val networkAnimalRepository: AnimalRepository by inject()
    private val networkImageLoader: ImageLoader by inject()
    private val networkAnimalBreedRepository: AnimalBreedRepository by inject()
    private val networkAnimalCharacteristicsRepository: AnimalCharacteristicsRepository by inject()
    private val navigator: KennelNavigation by inject()
    private val imageCompressor: ImageCompressor by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        getSomeImages = registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uriList: List<Uri> ->
            if (uriList.isNotEmpty()) {
                var indexForUriList = 0
                for (i in 0..3) {
                    val imgUri: Uri = uriList[indexForUriList]
                    val imageContainer = imageContainerList[i]
                    if (imageContainer.imageView.drawable != null) continue
                    imageContainer.uri = imgUri

                    coroutineScope.launch {
                        try {
                            imageContainer.overlay.isVisible = true
                            imageContainer.progressBar.isVisible = true
                            val resolver = requireContext().contentResolver
                            val imgInBytes =
                                resolver.openInputStream(imgUri)?.readBytes() ?: byteArrayOf()
                            val compressedImgInBytes = imageCompressor.compressImage(imgInBytes)
                            val imgInBitmap = BitmapFactory.decodeByteArray(
                                compressedImgInBytes,
                                0,
                                compressedImgInBytes.size
                            )
                            imageContainer.url = uploadImage(compressedImgInBytes)
                            imageContainer.imageView.setImageBitmap(imgInBitmap)
                            animalDto.photoNamesForCreate.add(imageContainer.url ?: "")
                            switchAddAndCancelBtnVisibility(true, imageContainer)
                            imageContainer.overlay.isVisible = false
                            imageContainer.progressBar.isVisible = false
                        } catch (ex: ServerErrorException) {
                            showError(SERVER_ERROR)
                        } catch (ex: SocketTimeoutException) {
                            val message = getString(R.string.server_unavailable_msg)
                            showError(message)
                        } catch (ex: ConnectException) {
                            val message = getString(R.string.server_unavailable_msg)
                            showError(message)
                        } catch (ex: BadTokenException) {
                            handleBadTokenException()
                        }
                    }
                    imageContainer.uri = uriList[indexForUriList]
                    indexForUriList++
                    if (indexForUriList == uriList.size) break
                }
            }
        }

    }

    private fun handleBadTokenException() {
        val userCity = userPropertiesRepository.getUserCity()
        userPropertiesRepository.removeAll()
        userPropertiesRepository.saveUserCity(userCity)
        val message = getString(R.string.bad_token_msg)
        showError(message)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor(true)
        setInsetsListenerForPadding(view, left = false, top = true, right = false, bottom = false)
        animalForChange = arguments?.getParcelable(BUNDLE_KEY_FOR_ANIMAL_OBJECT)
        isFromAnimalSettings = arguments
            ?.getBoolean(FROM_SETTINGS_FRAGMENT_KEY, false) ?: false

        if (isFromAnimalSettings) {
            animalDto.animalId = animalForChange?.id ?: 0
            animalDto.kennelId = animalForChange?.kennel?.id ?: 0
            animalDto.animalTypeId = animalForChange?.typeId ?: 0
        } else {
            animalDto.kennelId = arguments?.getInt(KENNEL_ID_KEY) ?: 0
            animalDto.animalTypeId = arguments?.getInt(ANIMAL_TYPE_ID_KEY) ?: 0
        }

        animalDto.personId = userPropertiesRepository.getUserId()
        initViews(view)
        setListeners()

        coroutineScope.launch {
            try {
                animalBreeds = networkAnimalBreedRepository.getAnimalBreeds(animalDto.animalTypeId)
                animalColors = networkAnimalCharacteristicsRepository.getAnimalColors()
                setAnimalsBreedToAdapter()
                setAnimalsColorsToAdapter()

                if (isFromAnimalSettings) {
                    setCurrentDataToInputs()
                }
            } catch (ex: EmptyBodyException) {
                val errorText = getString(R.string.server_error_msg)
                showError(errorText = errorText)
            } catch (ex: BadTokenException) {
                handleBadTokenException()
            } catch (ex: ServerErrorException) {
                val errorText = getString(R.string.server_unavailable_msg)
                showError(errorText = errorText)
            } catch (ex: ConnectException) {
                val errorText = getString(R.string.internet_failure_text)
                showError(errorText = errorText)
            } catch (ex: SocketTimeoutException) {
                val errorText = getString(R.string.internet_failure_text)
                showError(errorText = errorText)
            }
        }


    }

    private fun initViews(view: View) {
        scroll = view.findViewById(R.id.add_animal_scroll)
        addBtn = view.findViewById(R.id.add_animal_add_btn)
        descriptionInput = view.findViewById(R.id.add_animal_description_input)

        firstImage = view.findViewById(R.id.add_animal_first_animal_img)
        firstImageAddBtn = view.findViewById(R.id.add_animal_first_add_image_btn)
        firstImageCancelBtn = view.findViewById(R.id.add_animal_first_cancel_image_btn)
        firstImageOverlay = view.findViewById(R.id.add_animal_first_img_overlay)
        firstImageProgress = view.findViewById(R.id.add_animal_first_img_progress)
        imageContainerList.add(
            ImageContainer(
                0,
                null,
                null,
                firstImage,
                firstImageAddBtn,
                firstImageCancelBtn,
                firstImageProgress,
                firstImageOverlay
            )
        )

        secondImage = view.findViewById(R.id.add_animal_second_animal_img)
        secondImageAddBtn = view.findViewById(R.id.add_animal_second_add_btn)
        secondImageCancelBtn = view.findViewById(R.id.add_animal_second_cancel_image_btn)
        secondImageOverlay = view.findViewById(R.id.add_animal_second_img_overlay)
        secondImageProgress = view.findViewById(R.id.add_animal_second_img_progress)
        imageContainerList.add(
            ImageContainer(
                1,
                null,
                null,
                secondImage,
                secondImageAddBtn,
                secondImageCancelBtn,
                secondImageProgress,
                secondImageOverlay
            )
        )

        thirdImage = view.findViewById(R.id.add_animal_third_animal_img)
        thirdImageAddBtn = view.findViewById(R.id.add_animal_third_add_btn)
        thirdImageCancelBtn = view.findViewById(R.id.add_animal_third_cancel_image_btn)
        thirdImageOverlay = view.findViewById(R.id.add_animal_third_img_overlay)
        thirdImageProgress = view.findViewById(R.id.add_animal_third_img_progress)
        imageContainerList.add(
            ImageContainer(
                2,
                null,
                null,
                thirdImage,
                thirdImageAddBtn,
                thirdImageCancelBtn,
                thirdImageProgress,
                thirdImageOverlay
            )
        )

        fourthImage = view.findViewById(R.id.add_animal_fourth_animal_img)
        fourthImageAddBtn = view.findViewById(R.id.add_animal_fourth_add_btn)
        fourthImageCancelBtn = view.findViewById(R.id.add_animal_fourth_cancel_image_btn)
        fourthImageOverlay = view.findViewById(R.id.add_animal_third_fourth_overlay)
        fourthImageProgress = view.findViewById(R.id.add_animal_fourth_img_progress)
        imageContainerList.add(
            ImageContainer(
                3,
                null,
                null,
                fourthImage,
                fourthImageAddBtn,
                fourthImageCancelBtn,
                fourthImageProgress,
                fourthImageOverlay
            )
        )

        yearsContainer = view.findViewById(R.id.add_animal_years_container)
        yearsSlider = view.findViewById(R.id.add_animal_years_slider)
        yearsNum = view.findViewById(R.id.add_animal_years_number)
        yearsText = view.findViewById(R.id.add_animal_years_text)

        monthsContainer = view.findViewById(R.id.add_animal_months_container)
        monthsSlider = view.findViewById(R.id.add_animal_months_slider)
        monthsNum = view.findViewById(R.id.add_animal_months_number)
        monthsText = view.findViewById(R.id.add_animal_months_text)

        animalNameInputContainer = view.findViewById(R.id.add_animal_name_container)
        animalName = view.findViewById(R.id.add_animal_name_input)

        animalBreedsInput = view.findViewById(R.id.add_animal_breed_input)
        animalBreedsInputContainer = view.findViewById(R.id.add_animal_breed_container)
        animalColorInput = view.findViewById(R.id.add_animal_color_input)

        animalColorInputContainer = view.findViewById(R.id.add_animal_color_container)
        animalDescriptionInputContainer = view.findViewById(R.id.add_animal_description_container)

        maleBtn = view.findViewById(R.id.add_animal_male_button)
        femaleBtn = view.findViewById(R.id.add_animal_female_button)

    }

    private fun setListeners() {

        imageContainerList.forEach { image ->
            image.addBtn.setOnClickListener {
                changeImageErrorHighLight(false)
                getSomeImages.launch(IMAGE_TYPE)
            }
            image.cancelBtn.setOnClickListener {
                animalDto.photoNamesForDelete.add(image.url ?: "")
                animalDto.photoNamesForCreate.remove(image.url ?: "")
                deletedImage = image
                transferImages()
            }
        }

        yearsSlider.addOnChangeListener { _, value, _ ->
            setYears(value.toInt())
            changeAgeContainersErrorHighLight(false)
        }
        monthsSlider.addOnChangeListener { _, value, _ ->
            monthsNum.text = value.toInt().toString()
            changeAgeContainersErrorHighLight(false)
        }

        animalName.setOnFocusChangeListener { _, isFocused ->
            if (isFocused && animalNameInputContainer.isErrorEnabled) {
                animalNameInputContainer.isErrorEnabled = false
                animalNameInputContainer.error = ""
            }
        }

        animalBreedsInput.setOnFocusChangeListener { _, focused ->
            if (focused) {
                scroll.smoothScrollTo(0, addBtn.bottom)
            }
            if (animalBreedsInputContainer.isErrorEnabled) {
                animalBreedsInputContainer.isErrorEnabled = false
                animalBreedsInputContainer.error = ""
            }
        }

        animalBreedsInput.setOnItemClickListener { adapter, _, position, _ ->
            val selectedBreed = adapter.getItemAtPosition(position) as Breed
            animalDto.breedId = selectedBreed.id
        }

        animalColorInputContainer.setOnClickListener {

        }

        animalColorInput.setOnItemClickListener { adapter, _, position, _ ->
            val selectedColor = adapter.getItemAtPosition(position) as AnimalCharacteristic
            animalDto.colorCharacteristicId = selectedColor.id
        }

        animalColorInput.setOnClickListener {
            if (animalColorInputContainer.isErrorEnabled) {
                animalColorInputContainer.isErrorEnabled = false
                animalColorInputContainer.error = ""
            }
        }

        maleBtn.setOnClickListener {
            animalDto.genderId = Gender.MALE.genderId
            maleBtn.buttonTintList = ColorStateList
                .valueOf(requireContext().getColor(R.color.cian5))
            femaleBtn.buttonTintList = ColorStateList
                .valueOf(requireContext().getColor(R.color.cian5))
        }

        femaleBtn.setOnClickListener {
            animalDto.genderId = Gender.FEMALE.genderId
            maleBtn.buttonTintList = ColorStateList
                .valueOf(requireContext().getColor(R.color.cian5))
            femaleBtn.buttonTintList = ColorStateList
                .valueOf(requireContext().getColor(R.color.cian5))
        }

        descriptionInput.setOnFocusChangeListener { _, isFocused ->
            if (isFocused && animalDescriptionInputContainer.isErrorEnabled) {
                animalDescriptionInputContainer.isErrorEnabled = false
                animalDescriptionInputContainer.error = ""
            }
        }

        addBtn.setOnClickListener {

            coroutineScope.launch {
                try {
                    animalDto.description = descriptionInput.text.toString()
                    animalDto.years = yearsNum.text.toString().toInt()
                    animalDto.months = monthsNum.text.toString().toInt()
                    animalDto.name = animalName.text.toString()
                    val isValid = validateForm()
                    if (!isValid) showDialog()

                } catch (ex: Exception) {
                    Log.d("result", "Result was wrong")
                }
            }
        }
    }

    private fun setAnimalsColorsToAdapter() {
        val adapter =
            ArrayAdapter(requireContext(), R.layout.one_string_row, animalColors)
        animalColorInput.setAdapter(adapter)
    }

    private fun setAnimalsBreedToAdapter() {
        val adapter =
            ArrayAdapter(requireContext(), R.layout.one_string_row, animalBreeds)
        animalBreedsInput.setAdapter(adapter)
    }

    private fun setYears(value: Int) {
        val years = requireContext().resources.getString(R.string.add_animal_years_text)
        val year = requireContext().resources.getString(R.string.year)
        val yearWithLetterA = "${year}а"
        when (value) {
            1 -> {
                yearsNum.text = value.toString()
                yearsText.text = year
            }
            2, 3, 4 -> {
                yearsNum.text = value.toString()
                yearsText.text = yearWithLetterA
            }
            else -> {
                yearsNum.text = value.toString()
                yearsText.text = years
            }
        }
    }

    private fun transferImages() {
        for (i in deletedImage?.id!!..3) {
            val currentImageContainer = imageContainerList[i]
            if (i == imageContainerList.size - 1) {
                currentImageContainer.imageView.setImageDrawable(null)
                currentImageContainer.uri = null
                currentImageContainer.url = null
                switchAddAndCancelBtnVisibility(false, currentImageContainer)
                break
            }
            val nextImageContainer = imageContainerList[i + 1]

            val nextImg = nextImageContainer.imageView.drawable
            val nextUrl = nextImageContainer.url

            if (nextImg != null) {
                currentImageContainer.imageView.setImageDrawable(nextImg)
                currentImageContainer.url = nextUrl
                nextImageContainer.imageView.setImageDrawable(null)
                nextImageContainer.url = null
                switchAddAndCancelBtnVisibility(true, currentImageContainer)
            } else {
                currentImageContainer.imageView.setImageDrawable(null)
                currentImageContainer.uri = null
                currentImageContainer.url = null
                switchAddAndCancelBtnVisibility(false, currentImageContainer)
            }
        }
    }

    private fun validateForm(): Boolean {
        var hasInvalidValue = false
        val imageValidator = ImageValidator()
        if (imageValidator.hasPhotoChecker(animalDto.photoNamesForCreate)) {
            hasInvalidValue = true
            scroll.smoothScrollTo(0, firstImage.top)
            changeImageErrorHighLight(true)
            Toast.makeText(requireContext(), NO_PHOTO_ERROR, Toast.LENGTH_LONG).show()
        }

        if (animalDto.years == 0 && animalDto.months == 0) {
            if (!hasInvalidValue) scroll.smoothScrollTo(0, yearsContainer.top)
            hasInvalidValue = true
            changeAgeContainersErrorHighLight(true)
            Toast.makeText(requireContext(), NO_AGE_ERROR, Toast.LENGTH_LONG).show()
        }

        if (animalDto.name == "") {
            if (!hasInvalidValue && scroll.y < animalNameInputContainer.top) {
                scroll.smoothScrollTo(0, animalNameInputContainer.top)
                hasInvalidValue = true
            }
            animalNameInputContainer.isErrorEnabled = true
            animalNameInputContainer.error = EMPTY_NAME_ERROR
        }

        if (animalDto.breedId == 0) {
            hasInvalidValue = true
            animalBreedsInputContainer.isErrorEnabled = true
            animalBreedsInputContainer.error = NOT_EXISTED_BREED_ERROR
        }

        if (animalDto.colorCharacteristicId == 0) {
            hasInvalidValue = true
            animalColorInputContainer.isErrorEnabled = true
            animalColorInputContainer.error = EMPTY_COLOR_ERROR
        }

        if (animalDto.description == "") {
            hasInvalidValue = true
            animalDescriptionInputContainer.isErrorEnabled = true
            animalDescriptionInputContainer.error = EMPTY_DESCRIPTION_ERROR
        }

        if (animalDto.genderId == 0) {
            hasInvalidValue = true
            maleBtn.buttonTintList = ColorStateList
                .valueOf(requireContext().getColor(R.color.choice_color))
            femaleBtn.buttonTintList = ColorStateList
                .valueOf(requireContext().getColor(R.color.choice_color))
        }
        return hasInvalidValue
    }

    private fun changeImageErrorHighLight(isError: Boolean) {
        val errorBackground = requireContext().getDrawable(R.drawable.has_image_stroke)
        val noErrorBackground = requireContext().getDrawable(R.drawable.no_image_stroke)
        if (isError) {
            firstImage.background = errorBackground
            secondImage.background = errorBackground
            thirdImage.background = errorBackground
            fourthImage.background = errorBackground
        } else {
            firstImage.background = noErrorBackground
            secondImage.background = noErrorBackground
            thirdImage.background = noErrorBackground
            fourthImage.background = noErrorBackground
        }
    }

    private fun changeAgeContainersErrorHighLight(isError: Boolean) {
        val errorBackground = requireContext().getDrawable(R.drawable.has_age_container_stroke)
        val noErrorBackground = requireContext().getDrawable(R.drawable.animal_age_background)
        if (isError) {
            monthsContainer.background = errorBackground
            yearsContainer.background = errorBackground
        } else {
            monthsContainer.background = noErrorBackground
            yearsContainer.background = noErrorBackground
        }
    }

    private fun showDialog() {

        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)

            .setView(R.layout.add_animal_dialog)
            .show()

        val addOrUpdateText: TextView? = dialog.findViewById(R.id.add_animal_dialog_clarification)
        if (isFromAnimalSettings) {
            val addingAnimalText = getString(R.string.update)
            addOrUpdateText?.text = getString(
                R.string.add_animal_dialog_clarification_text,
                addingAnimalText
            )
        } else {
            val addingAnimalText = getString(R.string.add)
            addOrUpdateText?.text = getString(
                R.string.add_animal_dialog_clarification_text,
                addingAnimalText
            )
        }

        val positiveBtn: Button? = dialog.findViewById(R.id.add_animal_dialog_positive_btn)
        val negativeBtn: Button? = dialog.findViewById(R.id.add_animal_dialog_negative_btn)
        val spinner: Spinner? = dialog.findViewById(R.id.add_animal_dialog_spinner)

        positiveBtn?.setOnClickListener {
            coroutineScope.launch {
                spinner?.isVisible = true
                try {
                    val token = userPropertiesRepository.getUserToken()
                    if (isFromAnimalSettings) {
                        val animal: Animal = networkAnimalRepository.updateAnimal(
                            token = token,
                            addOrUpdateAnimalRequest = animalDto
                        )
                        spinner?.isVisible = false
                        dialog.dismiss()
                        isInsetsWorked = false
                        val bundle = Bundle()
                        bundle.putParcelable(BUNDLE_KEY_FOR_ANIMAL_OBJECT, animal)
                        setFragmentResult(FROM_ADD_ANIMAL_REQUEST_KEY, bundle)
                        navigator.backToPreviousFragment()
                    } else {
                        val animal: Animal = networkAnimalRepository.addNewAnimal(
                            token = token,
                            addOrUpdateAnimalRequest = animalDto
                        )
                        spinner?.isVisible = false
                        dialog.dismiss()
                        isInsetsWorked = false
                        val newAnimalBundle = Bundle()
                        newAnimalBundle.putParcelable(ANIMAL_BUNDLE_KEY, animal)
                        setFragmentResult(NEW_ANIMAL_ADDED_RESULT_KEY, newAnimalBundle)
                        navigator.backToPreviousFragment()
                    }

                } catch (ex: BadTokenException) {
                    dialog.dismiss()
                    showBadTokenDialog(userPropertiesRepository)
                } catch (ex: ServerErrorException) {
                    dialog.dismiss()
                    val message = getString(R.string.server_unavailable_msg)
                    showError(message)
                } catch (ex: ConnectException) {
                    dialog.dismiss()
                    val message = getString(R.string.internet_failure_text)
                    showError(message)
                } catch (ex: SocketTimeoutException) {
                    dialog.dismiss()
                    val message = getString(R.string.internet_failure_text)
                    showError(message)
                }
            }
        }

        negativeBtn?.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setCurrentDataToInputs() {
        animalForChange?.let { animal ->
            for (index in 0 until animal.imgUrl.size) {
                val currentContainer: ImageContainer = imageContainerList[index]
                val currentImgUrl: String = animal.imgUrl[index].url
                currentContainer.url = currentImgUrl
                animalDto.photoNamesForCreate.add(currentImgUrl)
                switchAddAndCancelBtnVisibility(true, currentContainer)
                val endpoint = resources.getString(R.string.animal_photo_endpoint)
                val dogPlaceholder = context?.getDrawable(R.drawable.dog_placeholder)!!
                networkImageLoader.setPhotoToView(
                    currentContainer.imageView,
                    endpoint,
                    currentImgUrl,
                    dogPlaceholder
                )
            }

            yearsSlider.value = (animal.age / 12).toFloat()
            monthsSlider.value = (animal.age % 12).toFloat()

            animalName.setText(animal.name)

            animalBreedsInput.setText(animal.breed)
            val breed = animalBreeds.find {
                it.name == animal.breed
            }
            animalDto.breedId = breed?.id ?: 0

            val color = animalColors.find {
                animal.characteristics["color"] == it.name
            }
            animalDto.colorCharacteristicId = color?.id ?: 0

            descriptionInput.setText(animal.description)

            when (animal.gender) {
                "Мальчик" -> {
                    maleBtn.isChecked = true
                    animalDto.genderId = 1
                }
                else -> {
                    femaleBtn.isChecked = true
                    animalDto.genderId = 2
                }
            }

        }
    }

    private fun switchAddAndCancelBtnVisibility(hasImage: Boolean, imageContainer: ImageContainer) {
        when (hasImage) {
            true -> {
                imageContainer.addBtn.isVisible = false
                imageContainer.cancelBtn.isVisible = true
            }
            else -> {
                imageContainer.addBtn.isVisible = true
                imageContainer.cancelBtn.isVisible = false
            }
        }
    }

    private suspend fun uploadImage(imageInBytes: ByteArray): String = withContext(Dispatchers.IO) {
        val token = userPropertiesRepository.getUserToken()
        return@withContext networkAnimalRepository.addPhotoToTmpDir(
            token = token,
            image = imageInBytes
        )
    }

    private fun showError(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }
}