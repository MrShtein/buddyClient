package mr.shtein.buddyandroidclient.screens.kennels

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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


class AddAnimalFragment : Fragment(R.layout.add_animal_fragment) {

    private lateinit var addBtn: Button
    private lateinit var descriptionContainer: TextInputLayout
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var hintGroup: Group
    private lateinit var imageAcceptHint: ShapeableImageView
    private lateinit var animalImagesGroup: Group
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
                hintGroup.visibility = View.GONE
                animalImagesGroup.visibility = View.VISIBLE
                for ((index, value) in uriList.withIndex()) {
                    val imageContainer = imageContainerList[index]
                    imageContainer.imageView.setImageURI(value)
                    imageContainer.addBtn.visibility = View.GONE
                    imageContainer.cancelBtn.visibility = View.VISIBLE
                }
            }
        }
        getOneImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                currentImage?.addBtn?.visibility = View.INVISIBLE
                currentImage?.cancelBtn?.visibility = View.VISIBLE
                currentImage?.imageView?.setImageURI(uri)
                currentImage = null
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

        hintGroup = view.findViewById(R.id.add_animal_hint_image_group)
        imageAcceptHint = view.findViewById(R.id.add_animal_image_accept_hint)

        animalImagesGroup = view.findViewById(R.id.add_animal_images_group)

        firstImage = view.findViewById(R.id.add_animal_first_animal_img)
        firstImageAddBtn = view.findViewById(R.id.add_animal_first_add_image_btn)
        firstImageCancelBtn = view.findViewById(R.id.add_animal_first_cancel_image_btn)
        imageContainerList.add(ImageContainer(firstImage, firstImageAddBtn, firstImageCancelBtn))

        secondImage = view.findViewById(R.id.add_animal_second_animal_img)
        secondImageAddBtn = view.findViewById(R.id.add_animal_second_add_btn)
        secondImageCancelBtn = view.findViewById(R.id.add_animal_second_cancel_image_btn)
        imageContainerList.add(ImageContainer(secondImage, secondImageAddBtn, secondImageCancelBtn))

        thirdImage = view.findViewById(R.id.add_animal_third_animal_img)
        thirdImageAddBtn = view.findViewById(R.id.add_animal_third_add_btn)
        thirdImageCancelBtn = view.findViewById(R.id.add_animal_third_cancel_image_btn)
        imageContainerList.add(ImageContainer(thirdImage, thirdImageAddBtn, thirdImageCancelBtn))

        fourthImage = view.findViewById(R.id.add_animal_fourth_animal_img)
        fourthImageAddBtn = view.findViewById(R.id.add_animal_fourth_add_btn)
        fourthImageCancelBtn = view.findViewById(R.id.add_animal_fourth_cancel_image_btn)
        imageContainerList.add(ImageContainer(fourthImage, fourthImageAddBtn, fourthImageCancelBtn))

        yearsSlider = view.findViewById(R.id.add_animal_years_slider)
        animalYearsNum = view.findViewById(R.id.add_animal_years_number)
        animalYearsText = view.findViewById(R.id.add_animal_years_text)

        monthsSlider = view.findViewById(R.id.add_animal_months_slider)
        animalMonthsNum = view.findViewById(R.id.add_animal_months_number)
        animalMonthsText = view.findViewById(R.id.add_animal_months_text)


    }

    private fun setListeners(view: View) {
        imageAcceptHint.setOnClickListener {
            getSomeImages.launch(IMAGE_TYPE)
            setNewConstraints(view)
        }

        imageContainerList.forEach { image ->
            image.addBtn.setOnClickListener {
                currentImage = image
                getOneImage.launch(IMAGE_TYPE)
            }
            image.cancelBtn.setOnClickListener {
                image.cancelBtn.visibility = View.INVISIBLE
                image.addBtn.visibility = View.VISIBLE
                image.imageView.setImageURI(null)
            }
        }

        yearsSlider.addOnChangeListener { _, value, _ ->
            setYears(value.toInt())
        }
        monthsSlider.addOnChangeListener { _, value, _ ->
            animalMonthsNum.text = value.toInt().toString()
        }


    }

    private fun setNewConstraints(view: View) {
        val constraintSet = ConstraintSet()
        val constraintLayout =
            view.findViewById(R.id.add_animal_main_container) as ConstraintLayout;
        constraintSet.clone(constraintLayout)
        constraintSet.clear(R.id.add_animal_age_label, ConstraintSet.TOP)
        val marginTop = requireContext().resources.getDimension(R.dimen.margin_for_add_kennel_text)
        constraintSet.connect(
            R.id.add_animal_age_label, ConstraintSet.TOP,
            R.id.add_animal_bottom_image_divider, ConstraintSet.BOTTOM,
            marginTop.toInt()
        )
        constraintSet.applyTo(constraintLayout)

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