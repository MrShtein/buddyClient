package mr.shtein.buddyandroidclient.screens.kennels

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.exceptions.validate.BadTokenException
import mr.shtein.buddyandroidclient.exceptions.validate.EmptyBodyException
import mr.shtein.buddyandroidclient.exceptions.validate.EmptyFieldException
import mr.shtein.buddyandroidclient.model.Gender
import mr.shtein.buddyandroidclient.model.ImageContainer
import mr.shtein.buddyandroidclient.model.dto.AnimalCharacteristic
import mr.shtein.buddyandroidclient.model.dto.AnimalType
import mr.shtein.buddyandroidclient.model.dto.Breed
import mr.shtein.buddyandroidclient.model.dto.NewAnimal
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.showBadTokenDialog
import mr.shtein.buddyandroidclient.utils.ImageValidator
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception

private const val IMAGE_TYPE = "image/*"
private const val NO_ANIMAL_TYPE_MSG = "Сначала необходимо выбрать питомца"
private const val NO_PHOTO_ERROR = "Необходимо добавить хотя бы одну фотографию"
private const val NO_AGE_ERROR = "Необходимо указать возраст питомца"
private const val EMPTY_TYPE_ERROR = "Необходимо выбрать тип питомца"
private const val EMPTY_NAME_ERROR = "Необходимо ввести имя питомца"
private const val NOT_EXISTED_BREED_ERROR = "Необходимо выбрать породу из списка"
private const val EMPTY_COLOR_ERROR = "Необходимо выбрать цвет питомца"
private const val EMPTY_DESCRIPTION_ERROR = "Необходимо ввести описание питомца"
private const val KENNEL_ID_KEY = "kennel_id"
const val SERVER_ERROR = "Что-то не так с сервером"
private const val FILE_NOT_FOUND_EXCEPTION_MSG = "К сожалению, файл не найден"
private const val COLOR_CHARACTERISTIC_ID = 1
private const val PART_NAME_FOR_FILES = "files"
private const val TOKEN_PREFIX = "Bearer"
private const val ANIMAL_TYPE_ID_KEY = "animal_type_id"



class AddAnimalFragment : Fragment(R.layout.add_animal_fragment) {

    private lateinit var scroll: NestedScrollView
    private lateinit var addBtn: Button
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var firstImage: ImageView
    private lateinit var firstImageAddBtn: ImageButton
    private lateinit var firstImageCancelBtn: ImageButton
    private lateinit var secondImage: ImageView
    private lateinit var secondImageAddBtn: ImageButton
    private lateinit var secondImageCancelBtn: ImageButton
    private lateinit var thirdImage: ImageView
    private lateinit var thirdImageAddBtn: ImageButton
    private lateinit var thirdImageCancelBtn: ImageButton
    private lateinit var fourthImage: ImageView
    private lateinit var fourthImageAddBtn: ImageButton
    private lateinit var fourthImageCancelBtn: ImageButton
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
    private lateinit var storage: SharedPreferences
    private val newAnimalDto = NewAnimal()
    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var imageContainerList = arrayListOf<ImageContainer>()
    private var animalBreeds: List<Breed> = mutableListOf()
    private var animalColors: List<AnimalCharacteristic> = listOf()
    private var currentImage: ImageContainer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSomeImages = registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uriList: List<Uri> ->
            if (uriList.isNotEmpty()) {
                var indexForUriList = 0
                for (i in 0..3) {
                    val imageContainer = imageContainerList[i]
                    if (imageContainer.imageView.drawable != null) continue
                    imageContainer.imageView.setImageURI(uriList[indexForUriList])
                    imageContainer.uri = uriList[indexForUriList]
                    imageContainer.addBtn.visibility = View.GONE
                    imageContainer.cancelBtn.visibility = View.VISIBLE
                    indexForUriList++
                    if (indexForUriList == uriList.size) break
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        newAnimalDto.kennelId = arguments?.getInt(KENNEL_ID_KEY) ?: 0
        newAnimalDto.animalTypeId = arguments?.getInt(ANIMAL_TYPE_ID_KEY) ?: 0
        newAnimalDto.personId = storage.readLong(SharedPreferences.USER_ID_KEY, 0)
        initViews(view)
        setListeners(view)

        coroutineScope.launch {
            try {
                animalBreeds = getAnimalBreeds(newAnimalDto.animalTypeId)
                animalColors = getAnimalColors(COLOR_CHARACTERISTIC_ID)
                setAnimalsBreedToAdapter()
                setAnimalsColorsToAdapter()
            } catch (ex: Exception) {
                Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
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
        imageContainerList.add(
            ImageContainer(
                0,
                null,
                firstImage,
                firstImageAddBtn,
                firstImageCancelBtn
            )
        )

        secondImage = view.findViewById(R.id.add_animal_second_animal_img)
        secondImageAddBtn = view.findViewById(R.id.add_animal_second_add_btn)
        secondImageCancelBtn = view.findViewById(R.id.add_animal_second_cancel_image_btn)
        imageContainerList.add(
            ImageContainer(
                1,
                null,
                secondImage,
                secondImageAddBtn,
                secondImageCancelBtn
            )
        )

        thirdImage = view.findViewById(R.id.add_animal_third_animal_img)
        thirdImageAddBtn = view.findViewById(R.id.add_animal_third_add_btn)
        thirdImageCancelBtn = view.findViewById(R.id.add_animal_third_cancel_image_btn)
        imageContainerList.add(
            ImageContainer(
                2,
                null,
                thirdImage,
                thirdImageAddBtn,
                thirdImageCancelBtn
            )
        )

        fourthImage = view.findViewById(R.id.add_animal_fourth_animal_img)
        fourthImageAddBtn = view.findViewById(R.id.add_animal_fourth_add_btn)
        fourthImageCancelBtn = view.findViewById(R.id.add_animal_fourth_cancel_image_btn)
        imageContainerList.add(
            ImageContainer(
                3,
                null,
                fourthImage,
                fourthImageAddBtn,
                fourthImageCancelBtn
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

    private fun setListeners(view: View) {

        imageContainerList.forEach { image ->
            image.addBtn.setOnClickListener {
                changeImageErrorHighLight(false)
                currentImage = image
                getSomeImages.launch(IMAGE_TYPE)
            }
            image.cancelBtn.setOnClickListener {
                currentImage = image
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

        animalName.setOnFocusChangeListener {_, isFocused ->
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
            newAnimalDto.breedId = selectedBreed.id
        }

        animalColorInput.setOnItemClickListener { adapter, _, position, _ ->
            val selectedColor = adapter.getItemAtPosition(position) as AnimalCharacteristic
            newAnimalDto.colorCharacteristicId = selectedColor.id
        }

        animalColorInput.setOnClickListener {
            if (animalColorInputContainer.isErrorEnabled) {
                animalColorInputContainer.isErrorEnabled = false
                animalColorInputContainer.error = ""
            }
        }

        maleBtn.setOnClickListener {
            newAnimalDto.genderId = Gender.MALE.ordinal
            maleBtn.buttonTintList = ColorStateList
                .valueOf(requireContext().getColor(R.color.cian5))
            femaleBtn.buttonTintList = ColorStateList
                .valueOf(requireContext().getColor(R.color.cian5))
        }

        femaleBtn.setOnClickListener {
            newAnimalDto.genderId = Gender.FEMALE.ordinal
            maleBtn.buttonTintList = ColorStateList
                .valueOf(requireContext().getColor(R.color.cian5))
            femaleBtn.buttonTintList = ColorStateList
                .valueOf(requireContext().getColor(R.color.cian5))
        }

        descriptionInput.setOnFocusChangeListener {_, isFocused ->
            if (isFocused && animalDescriptionInputContainer.isErrorEnabled) {
                animalDescriptionInputContainer.isErrorEnabled = false
                animalDescriptionInputContainer.error = ""
            }
        }

        addBtn.setOnClickListener {

            coroutineScope.launch {
                try {
                    newAnimalDto.description = descriptionInput.text.toString()
                    newAnimalDto.years = yearsNum.text.toString().toInt()
                    newAnimalDto.months = monthsNum.text.toString().toInt()
                    newAnimalDto.name = animalName.text.toString()
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
            ArrayAdapter(requireContext(), R.layout.animal_type_or_breed_row, animalColors)
        animalColorInput.setAdapter(adapter)
    }

    private fun setAnimalsBreedToAdapter() {
        val adapter =
            ArrayAdapter(requireContext(), R.layout.animal_type_or_breed_row, animalBreeds)
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
        for (i in currentImage?.id!!..3) {
            val currentImageContainer = imageContainerList[i]
            if (i == imageContainerList.size - 1) {
                currentImageContainer.imageView.setImageDrawable(null)
                currentImageContainer.uri = null
                currentImageContainer.addBtn.visibility = View.VISIBLE
                currentImageContainer.cancelBtn.visibility = View.INVISIBLE
                break
            }
            val nextImageContainer = imageContainerList[i + 1]

            val nextImg = nextImageContainer.imageView.drawable

            if (nextImg != null) {
                currentImageContainer.imageView.setImageDrawable(nextImg)
                currentImageContainer.addBtn.visibility = View.INVISIBLE
                currentImageContainer.cancelBtn.visibility = View.VISIBLE
            } else {
                currentImageContainer.imageView.setImageDrawable(null)
                currentImageContainer.uri = null
                currentImageContainer.addBtn.visibility = View.VISIBLE
                currentImageContainer.cancelBtn.visibility = View.INVISIBLE
            }
        }
    }

    private suspend fun getAnimalTypes(): List<AnimalType> = withContext(Dispatchers.IO) {
        val retrofitService = Common.retrofitService
        val response = retrofitService.getAnimalsType()
        if (response.isSuccessful) {
            return@withContext response.body() ?: throw EmptyBodyException(SERVER_ERROR)
        } else {
            throw Exception() //TODO разобраться с ошибками
        }

    }

    private suspend fun getAnimalBreeds(animalType: Int): List<Breed> =
        withContext(Dispatchers.IO) {
            if (animalType != 0) {
                val token = storage.readString(SharedPreferences.TOKEN_KEY, "")
                val retrofitService = Common.retrofitService
                val response = retrofitService.getAnimalsBreed("$TOKEN_PREFIX $token", animalType)
                if (response.isSuccessful) {
                    return@withContext response.body() ?: throw EmptyBodyException(SERVER_ERROR)
                } else {
                    when (response.code()) {
                        403 -> {
                            storage.writeString(SharedPreferences.TOKEN_KEY, "")
                            val msg = requireContext().resources.getString(R.string.elder_token_msg)
                            throw BadTokenException(msg)
                        }
                        else -> throw Exception("Что-то не так с сервером")
                        //TODO разобраться с ошибками
                    }

                }
            } else {
                throw EmptyFieldException(NO_ANIMAL_TYPE_MSG)
            }
        }

    private suspend fun getAnimalColors(colorId: Int): List<AnimalCharacteristic> =
        withContext(Dispatchers.IO) {
            val token = storage.readString(SharedPreferences.TOKEN_KEY, "")
            val retrofitService = Common.retrofitService
            val response = retrofitService.getAnimalsCharacteristicByCharacteristicTypeId(
                "$TOKEN_PREFIX $token", colorId
            )
            if (response.isSuccessful) {
                return@withContext response.body() ?: throw EmptyBodyException(SERVER_ERROR)
            } else {
                when (response.code()) {
                    403 -> {
                        storage.writeString(SharedPreferences.TOKEN_KEY, "")
                        val msg = requireContext().resources.getString(R.string.elder_token_msg)
                        throw BadTokenException(msg)
                    }
                    else -> throw Exception("Что-то не так с сервером")
                    //TODO разобраться с ошибками
                }

            }
        }

    private suspend fun addNewAnimal() = withContext(Dispatchers.IO) {

        val token = "$TOKEN_PREFIX ${storage.readString(SharedPreferences.TOKEN_KEY, "")}"
        val retrofit = Common.retrofitService
        val files: ArrayList<MultipartBody.Part> = arrayListOf()
        imageContainerList.forEach { imageContainer ->
            val uri = imageContainer.uri
            uri?.let {
                files.add(prepareFilePart(it, imageContainer.id))
            }
        }
        return@withContext retrofit.addNewAnimal(token, files, newAnimalDto)
    }

    private fun prepareFilePart(fileUri: Uri, fileId: Int): MultipartBody.Part {
        val contentResolver = requireContext().contentResolver
        val fileType = contentResolver.getType(fileUri) ?: ""
        val tmpFileName = "${fileId}.tmp"
        val dir = requireContext().filesDir
        val file = File(dir, tmpFileName)
        file.writeBytes(contentResolver.openInputStream(fileUri)?.readBytes() ?: byteArrayOf())
        if (file.totalSpace > 0) {
            val requestBody: RequestBody = RequestBody.create(
                MediaType.parse(fileType), file
            )
            return MultipartBody.Part.createFormData(PART_NAME_FOR_FILES, "file", requestBody)
        }
        throw FileNotFoundException(FILE_NOT_FOUND_EXCEPTION_MSG)

    }

    private fun validateForm(): Boolean {
        var hasInvalidValue = false
        val imageValidator = ImageValidator()
        if (imageValidator.hasPhotoChecker(imageContainerList)) {
            hasInvalidValue = true
            scroll.smoothScrollTo(0, firstImage.top)
            changeImageErrorHighLight(true)
            Toast.makeText(requireContext(), NO_PHOTO_ERROR, Toast.LENGTH_LONG).show()
        }

        if (newAnimalDto.years == 0 && newAnimalDto.months == 0) {
            if (!hasInvalidValue) scroll.smoothScrollTo(0, yearsContainer.top)
            hasInvalidValue = true
            changeAgeContainersErrorHighLight(true)
            Toast.makeText(requireContext(), NO_AGE_ERROR, Toast.LENGTH_LONG).show()
        }

        if (newAnimalDto.name == "") {
            if (!hasInvalidValue && scroll.y < animalNameInputContainer.top) {
                scroll.smoothScrollTo(0, animalNameInputContainer.top)
                hasInvalidValue = true
            }
            animalNameInputContainer.isErrorEnabled = true
            animalNameInputContainer.error = EMPTY_NAME_ERROR
        }

        if (newAnimalDto.breedId == 0) {
            hasInvalidValue = true
            animalBreedsInputContainer.isErrorEnabled = true
            animalBreedsInputContainer.error = NOT_EXISTED_BREED_ERROR
        }

        if (newAnimalDto.colorCharacteristicId == 0) {
            hasInvalidValue = true
            animalColorInputContainer.isErrorEnabled = true
            animalColorInputContainer.error = EMPTY_COLOR_ERROR
        }

        if (newAnimalDto.description == "") {
            hasInvalidValue = true
            animalDescriptionInputContainer.isErrorEnabled = true
            animalDescriptionInputContainer.error = EMPTY_DESCRIPTION_ERROR
        }

        if (newAnimalDto.genderId == 0) {
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
            .setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
            .show()

        val positiveBtn: Button? = dialog.findViewById(R.id.add_animal_dialog_positive_btn)
        val negativeBtn: Button? = dialog.findViewById(R.id.add_animal_dialog_negative_btn)
        val spinner: Spinner? = dialog.findViewById(R.id.add_animal_dialog_spinner)

        positiveBtn?.setOnClickListener {
            coroutineScope.launch {
                spinner?.isVisible = true
                try {
                    val result = addNewAnimal()
                    when (result.code()) {
                        201 -> {
                            spinner?.isVisible = false
                            dialog.dismiss()
                            findNavController().popBackStack()
                        }
                        403 -> {
                            val badTokenMsg = requireContext().getString(R.string.elder_token_msg)
                            throw BadTokenException(badTokenMsg)
                        }
                    }

                } catch (ex: Exception) {
                    dialog.dismiss()
                    Log.d("server", SERVER_ERROR)
                    showBadTokenDialog()
                }
            }
        }

        negativeBtn?.setOnClickListener {
            dialog.dismiss()
        }
    }
}

//TODO Сделать edge-to-edge fragment
//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun addAnimationWhenKeyboardAppear(view: View): WindowInsetsAnimation.Callback {
//        var startBottom = 0
//        var endBottom = 0
///home/mrshtein/Projects/AndroidStudioProjects/Tests/app/src/main/res/layout
//        return object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
//            override fun onPrepare(animation: WindowInsetsAnimation) {
//                startBottom = view.bottom
//            }
//
//            override fun onStart(
//                animation: WindowInsetsAnimation,
//                bounds: WindowInsetsAnimation.Bounds
//            ): WindowInsetsAnimation.Bounds {
//
//                return bounds
//            }
//
//            override fun onProgress(
//                insets: WindowInsets,
//                p1: MutableList<WindowInsetsAnimation>
//            ): WindowInsets {
//
//                Log.d("insets", "scrollPosition - ${scroll.scrollY}")
//                return insets
//
//            }
//
//            override fun onEnd(animation: WindowInsetsAnimation) {
//                super.onEnd(animation)
//            }
//
//        }
//    }

