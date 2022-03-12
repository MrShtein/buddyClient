package mr.shtein.buddyandroidclient.screens.kennels

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.model.ImageContainer

private const val IMAGE_TYPE = "image/*"
private const val TOO_MANY_IMG_UPLOAD_MSG = "Максиальное число фото равняется 4"


class AddAnimalFragment : Fragment(R.layout.add_animal_fragment) {

    private lateinit var addBtn: Button
    private lateinit var descriptionContainer: TextInputLayout
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
    private lateinit var animalYearsNum: TextView
    private lateinit var animalYearsText: TextView
    private lateinit var monthsSlider: Slider
    private lateinit var animalMonthsNum: TextView
    private lateinit var animalMonthsText: TextView
    private lateinit var getSomeImages: ActivityResultLauncher<String>
    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var getOneImage: ActivityResultLauncher<String>
    private var uriForImage: Uri? = null
    private var imageContainerList = arrayListOf<ImageContainer>()
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
        initViews(view)
        setListeners(view)
    }


    private fun initViews(view: View) {
        addBtn = view.findViewById(R.id.add_animal_add_btn)
        descriptionContainer = view.findViewById(R.id.add_animal_description_container)
        descriptionInput = view.findViewById(R.id.add_animal_description_input)

        firstImage = view.findViewById(R.id.add_animal_first_animal_img)
        firstImageAddBtn = view.findViewById(R.id.add_animal_first_add_image_btn)
        firstImageCancelBtn = view.findViewById(R.id.add_animal_first_cancel_image_btn)
        imageContainerList.add(ImageContainer(0, firstImage, firstImageAddBtn, firstImageCancelBtn))

        secondImage = view.findViewById(R.id.add_animal_second_animal_img)
        secondImageAddBtn = view.findViewById(R.id.add_animal_second_add_btn)
        secondImageCancelBtn = view.findViewById(R.id.add_animal_second_cancel_image_btn)
        imageContainerList.add(
            ImageContainer(
                1,
                secondImage,
                secondImageAddBtn,
                secondImageCancelBtn
            )
        )

        thirdImage = view.findViewById(R.id.add_animal_third_animal_img)
        thirdImageAddBtn = view.findViewById(R.id.add_animal_third_add_btn)
        thirdImageCancelBtn = view.findViewById(R.id.add_animal_third_cancel_image_btn)
        imageContainerList.add(ImageContainer(2, thirdImage, thirdImageAddBtn, thirdImageCancelBtn))

        fourthImage = view.findViewById(R.id.add_animal_fourth_animal_img)
        fourthImageAddBtn = view.findViewById(R.id.add_animal_fourth_add_btn)
        fourthImageCancelBtn = view.findViewById(R.id.add_animal_fourth_cancel_image_btn)
        imageContainerList.add(
            ImageContainer(
                3,
                fourthImage,
                fourthImageAddBtn,
                fourthImageCancelBtn
            )
        )

        yearsSlider = view.findViewById(R.id.add_animal_years_slider)
        animalYearsNum = view.findViewById(R.id.add_animal_years_number)
        animalYearsText = view.findViewById(R.id.add_animal_years_text)

        monthsSlider = view.findViewById(R.id.add_animal_months_slider)
        animalMonthsNum = view.findViewById(R.id.add_animal_months_number)
        animalMonthsText = view.findViewById(R.id.add_animal_months_text)


    }

    private fun setListeners(view: View) {

        imageContainerList.forEach { image ->
            image.addBtn.setOnClickListener {
                currentImage = image
                getSomeImages.launch(IMAGE_TYPE)
            }
            image.cancelBtn.setOnClickListener {
                image.cancelBtn.visibility = View.INVISIBLE
                image.addBtn.visibility = View.VISIBLE
                currentImage = image
                transferImages()
            }
        }

        yearsSlider.addOnChangeListener { _, value, _ ->
            setYears(value.toInt())
        }
        monthsSlider.addOnChangeListener { _, value, _ ->
            animalMonthsNum.text = value.toInt().toString()
        }


    }

    private fun setYears(value: Int) {
        val years = requireContext().resources.getString(R.string.add_animal_years_text)
        val year = requireContext().resources.getString(R.string.year)
        val yearWithLetterA = "${year}а"
        when (value) {
            1 -> {
                animalYearsNum.text = value.toString()
                animalYearsText.text = year
            }
            2, 3, 4 -> {
                animalYearsNum.text = value.toString()
                animalYearsText.text = yearWithLetterA
            }
            else -> {
                animalYearsNum.text = value.toString()
                animalYearsText.text = years
            }
        }
    }

    private fun transferImages() {
        for (i in currentImage?.id!!..3) {
            val currentImageContainer = imageContainerList[i]
            if (i == imageContainerList.size - 1) {
                currentImageContainer.imageView.setImageURI(null)
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
                currentImageContainer.addBtn.visibility = View.VISIBLE
                currentImageContainer.cancelBtn.visibility = View.INVISIBLE
            }
        }
    }

    //TODO Сделать edge-to-edge fragment
//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun addAnimationWhenKeyboardAppear(view: View): WindowInsetsAnimation.Callback {
//        var startBottom = 0
//        var endBottom = 0
//
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

}