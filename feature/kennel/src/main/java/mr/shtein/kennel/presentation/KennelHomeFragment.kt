package mr.shtein.kennel.presentation

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.*
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.mapper.AnimalMapper
import mr.shtein.data.model.Animal
import mr.shtein.data.model.KennelPreview
import mr.shtein.data.repository.AnimalRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.adapter.CatPhotoAdapter
import mr.shtein.kennel.presentation.adapter.DogPhotoAdapter
import mr.shtein.model.AnimalDTO
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.setStatusBarColor
import org.koin.android.ext.android.inject
import java.net.ConnectException
import java.net.SocketTimeoutException

private const val KENNEL_ITEM_BUNDLE_KEY = "kennel_item_key"
private const val DOG_ID = 1
private const val CAT_ID = 2
private const val RESULT_LISTENER_KEY = "result_key"
private const val RESULT_LISTENER_BUNDLE_KEY = "message_from_animal_card"


class KennelHomeFragment : Fragment(R.layout.kennel_home_fragment) {

    private lateinit var kennelAvatar: ImageView
//    private lateinit var personAvatar: ShapeableImageView
//    private lateinit var settingsBtn: ImageButton
    private lateinit var kennelName: TextView
    private lateinit var volunteersAmount: TextView
    private lateinit var animalsAmount: TextView
    private lateinit var dogsAmount: TextView
    private lateinit var addDogsBtn: ImageButton
    private lateinit var dogCarousel: RecyclerView
    private lateinit var catsAmount: TextView
    private lateinit var addCatsBtn: ImageButton
    private lateinit var catCarousel: RecyclerView
    private lateinit var dogAdapter: DogPhotoAdapter
    private lateinit var catAdapter: CatPhotoAdapter
    private lateinit var token: String
    private lateinit var dogsList: MutableList<Animal>
    private lateinit var catsList: MutableList<Animal>
    private val coroutine = CoroutineScope(Dispatchers.Main + Job())
    private val networkImageLoader: ImageLoader by inject()
    private val networkAnimalRepository: AnimalRepository by inject()
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val animalMapper: AnimalMapper by inject()
    private val navigator: KennelNavigation by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        setFragmentResultListener(RESULT_LISTENER_KEY) { _, bundle ->
            val messageAboutSomeChange = bundle.getString(RESULT_LISTENER_BUNDLE_KEY)
            Toast.makeText(context, messageAboutSomeChange, Toast.LENGTH_LONG).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor(false)
        val kennelItem: KennelPreview = arguments?.getParcelable(
            KENNEL_ITEM_BUNDLE_KEY
        )!!

        initViews(view)
        setValuesToViews(kennelItem)
        setListeners(kennelItem)

    }

    private fun initViews(view: View) {
        kennelAvatar = view.findViewById(R.id.kennel_home_avatar)
//        personAvatar = view.findViewById(R.id.kennel_home_person_avatar)
//        settingsBtn = view.findViewById(R.id.kennel_home_settings_btn)
        kennelName = view.findViewById(R.id.kennel_home_name)
        volunteersAmount = view.findViewById(R.id.kennel_home_volunteers_amount)
        animalsAmount = view.findViewById(R.id.kennel_home_animals_amount)
        dogsAmount = view.findViewById(R.id.kennel_home_dogs_amount)
        addDogsBtn = view.findViewById(R.id.kennel_home_add_dogs_btn)
        dogCarousel = view.findViewById(R.id.kennel_home_dog_carousel)
        catsAmount = view.findViewById(R.id.kennel_home_cats_amount)
        addCatsBtn = view.findViewById(R.id.kennel_home_add_cats_btn)
        catCarousel = view.findViewById(R.id.kennel_home_cat_carousel)
        dogCarousel = view.findViewById(R.id.kennel_home_dog_carousel)
    }

    private fun setValuesToViews(kennelItem: KennelPreview) {
        val endpoint = getString(R.string.kennel_avatar_endpoint)
        val photoName = kennelItem.avatarUrl
        val dogPlaceholder = context?.getDrawable(R.drawable.dog_placeholder)
        token = userPropertiesRepository.getUserToken()
        networkImageLoader.setPhotoToView(
            kennelAvatar,
            endpoint,
            photoName,
            dogPlaceholder
        )

        val uriForUserToken = userPropertiesRepository.getUserUri()
//        val personAvatarPlaceholder = context?.getDrawable(R.drawable.light_person_placeholder)
//        networkImageLoader.setPhotoToView(
//            personAvatar,
//            uriForUserToken,
//            personAvatarPlaceholder!!
//        )

        kennelName.text = kennelItem.name
        volunteersAmount.text = makeVolunteersText(kennelItem.volunteersAmount)
        animalsAmount.text = getAnimalCountText(kennelItem.animalsAmount)

        coroutine.launch {
            val kennelId = kennelItem.kennelId
            try {
                val dogType = requireContext()
                    .resources.getString(R.string.dogs)
                val catType = requireContext()
                    .resources.getString(R.string.cats)
                dogsList = loadAnimals(kennelId, dogType)
                catsList = loadAnimals(kennelId, catType)
                animalsAmount.text = makeAnimalText(dogsList.size + catsList.size)

                dogCarousel.setHasFixedSize(true)
                dogAdapter = DogPhotoAdapter(
                    dogsList,
                    token,
                    object : DogPhotoAdapter.OnAnimalItemClickListener {
                        override fun onClick(animalItem: Animal) {
                            navigator.moveToAnimalSettings(animal = animalItem)
                        }
                    },
                    networkImageLoader
                )
                dogCarousel.adapter = dogAdapter
                dogCarousel.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val dogCarouselHelper = LinearSnapHelper()
                dogCarouselHelper.attachToRecyclerView(dogCarousel)
                if (dogsList.isNotEmpty()) dogCarousel.visibility = View.VISIBLE


                catCarousel.setHasFixedSize(true)
                catAdapter = CatPhotoAdapter(
                    catsList,
                    token,
                    object : CatPhotoAdapter.OnAnimalItemClickListener {
                        override fun onClick(animalItem: Animal) {
                            navigator.moveToAnimalSettings(animal = animalItem)
                        }
                    },
                    networkImageLoader
                )
                catCarousel.adapter = catAdapter
                catCarousel.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val catCarouselHelper = LinearSnapHelper()
                catCarouselHelper.attachToRecyclerView(catCarousel)
                if (catsList.isNotEmpty()) catCarousel.visibility = View.VISIBLE

                catsAmount.text = getAnimalCountText(catsList.size)
                dogsAmount.text = getAnimalCountText(dogsList.size)


            } catch (ex: ConnectException) {
                val errorText = requireContext().getString(R.string.internet_failure_text)
                showError(errorText = errorText)
            } catch (ex: SocketTimeoutException) {
                val errorText = requireContext().getString(R.string.internet_failure_text)
                showError(errorText = errorText)
            } catch (ex: ServerErrorException) {
                val errorText = requireContext().getString(R.string.server_unavailable_msg)
                showError(errorText = errorText)
            }
        }
    }

    private fun makeAnimalText(amount: Int): String {
        return resources.getQuantityString(
            R.plurals.animal_found_count,
            amount,
            amount
        )
    }

    private fun setListeners(kennelItem: KennelPreview) {
        addDogsBtn.setOnClickListener {
            val kennelId = kennelItem.kennelId
            if (kennelItem.isValid) {
                navigator.moveToAddAnimalFromKennelHome(kennelId = kennelId, animalTypeId = DOG_ID)
            } else {
                showKennelIsNotValidDialog()
            }
        }

        addCatsBtn.setOnClickListener {
            val kennelId = kennelItem.kennelId
            if (kennelItem.isValid) {
                navigator.moveToAddAnimalFromKennelHome(kennelId = kennelId, animalTypeId = CAT_ID)
                } else {
                showKennelIsNotValidDialog()
            }
        }

    }

    private fun makeVolunteersText(amount: Int): String {
        val mainText = "волонтер"
        return when (amount) {
            1 -> "1 $mainText"
            2, 3, 4 -> "$amount ${mainText}а"
            else -> "$amount ${mainText}ов"
        }
    }

    private suspend fun loadAnimals(kennelId: Int, animalType: String): MutableList<Animal> =
        withContext(Dispatchers.IO) {
            val token = userPropertiesRepository.getUserToken()
            val response = networkAnimalRepository.getAnimalsByKennelIdAndAnimalType(
                token, kennelId, animalType
            )
            val animalDTOList: List<AnimalDTO> = response
            return@withContext animalMapper
                .transformFromDTOList(animalDTOList = animalDTOList)
                .toMutableList()
        }

    private fun getAnimalCountText(amount: Int): String {
        return resources.getQuantityString(
            R.plurals.buddy_found_count,
            amount,
            amount
        )
    }

    private fun showKennelIsNotValidDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)
            .setView(R.layout.not_valid_kennel_dialog)
            //.setBackground(ColorDrawable(requireContext().getColor((R.color.transparent))))
            .show()
        val okBtn: Button? = dialog.findViewById(R.id.not_valid_kennel_dialog_ok_btn)
        okBtn?.setOnClickListener {
            dialog.cancel()
        }
    }

    private fun showError(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }
}