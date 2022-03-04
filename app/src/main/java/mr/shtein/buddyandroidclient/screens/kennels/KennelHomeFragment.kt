package mr.shtein.buddyandroidclient.screens.kennels

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import kotlinx.coroutines.*
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.CatPhotoAdapter
import mr.shtein.buddyandroidclient.adapters.DogPhotoAdapter
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.model.KennelPreview
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import java.lang.Exception

class KennelHomeFragment : Fragment(R.layout.kennel_home_fragment) {

    private lateinit var kennelAvatar: ImageView
    private lateinit var personAvatar: ShapeableImageView
    private lateinit var settingsBtn: ImageButton
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
    private lateinit var storage: SharedPreferences
    private lateinit var token: String
    private lateinit var dogsList: MutableList<Animal>
    private lateinit var catsList: MutableList<Animal>
    private val coroutine = CoroutineScope(Dispatchers.Main + Job())

    companion object {
        private const val KENNEL_ITEM_BUNDLE_KEY = "kennel_item_key"
        private const val KENNEL_AVATAR_ENDPOINT = "http://10.0.2.2:8881/api/v1/kennel/avatar/"
        private const val KENNEL_ID_KEY = "kennel_id"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val kennelItemJson: String = arguments?.get(KENNEL_ITEM_BUNDLE_KEY) as String
        val kennelItem: KennelPreview = Gson().fromJson(kennelItemJson, KennelPreview::class.java)

        initViews(view)
        setValuesToViews(kennelItem)
        setListeners(kennelItem)

    }

    private fun initViews(view: View) {
        kennelAvatar = view.findViewById(R.id.kennel_home_avatar)
        personAvatar = view.findViewById(R.id.kennel_home_person_avatar)
        settingsBtn = view.findViewById(R.id.kennel_home_settings_btn)
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
        storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
    }

    private fun setValuesToViews(kennelItem: KennelPreview) {
        val urlForKennelAvatar = "$KENNEL_AVATAR_ENDPOINT${kennelItem.avatarUrl}"
        token = storage.readString(SharedPreferences.TOKEN_KEY, "")
        val header = LazyHeaders.Builder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        val urlForGlide = GlideUrl(
            urlForKennelAvatar,
            header
        )
        Glide.with(this)
            .load(urlForGlide)
            .into(kennelAvatar)

        val uriForUserToken = storage.readString(SharedPreferences.USER_AVATAR_URI_KEY, "")
        if (uriForUserToken != "") {
            try {
                Glide.with(this)
                    .load(uriForUserToken)
                    .into(personAvatar)
            } catch (ex: Exception) {
                Log.e("error", "Не удалось загрузить аватарку пользователя")
                personAvatar.visibility = View.INVISIBLE
            }
        } else {
            personAvatar.visibility = View.INVISIBLE
        }

        kennelName.text = kennelItem.name
        volunteersAmount.text = makeVolunteersText(kennelItem.volunteersAmount)
        animalsAmount.text = makeAnimalText(kennelItem.animalsAmount)

       coroutine.launch {
           val kennelId = kennelItem.kennelId
           try {
               val dogType = requireContext()
                   .resources.getString(R.string.kennel_home_dogs_label_text)
               val catType = requireContext()
                   .resources.getString(R.string.kennel_home_cats_label_text)
               dogsList = loadAnimals(kennelId, dogType)
               catsList = loadAnimals(kennelId, catType)

               dogCarousel.visibility = View.VISIBLE
               dogCarousel.setHasFixedSize(true)
               dogAdapter = DogPhotoAdapter(dogsList, token, object : DogPhotoAdapter.OnAnimalItemClickListener {
                   override fun onClick(animalItem: Animal) {
                       Toast.makeText(requireContext(), "text", Toast.LENGTH_LONG).show()
                   }
               })
               dogCarousel.adapter = dogAdapter
               dogCarousel.layoutManager =
                   LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

               catCarousel.visibility = View.VISIBLE
               catCarousel.setHasFixedSize(true)
               catAdapter = CatPhotoAdapter(catsList, token, object : CatPhotoAdapter.OnAnimalItemClickListener {
                   override fun onClick(animalItem: Animal) {
                       Toast.makeText(requireContext(), "text", Toast.LENGTH_LONG).show()
                   }
               })
               catCarousel.adapter = catAdapter
               catCarousel.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                   catsAmount.text = getAnimalCountText(catsList.size)
                   dogsAmount.text = getAnimalCountText(dogsList.size)



           } catch (ex: Exception) {
               Log.e("error", "Что-то пошло не так")
           }
        }
    }

    private fun setListeners(kennelItem: KennelPreview) {
        addDogsBtn.setOnClickListener {
            val kennelId = kennelItem.kennelId
            val bundle = bundleOf(KENNEL_ID_KEY to kennelId)
            if (kennelItem.isValid) {
                findNavController().navigate(
                    R.id.action_kennelHomeFragment_to_addAnimalFragment,
                    bundle
                )
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

    private fun makeAnimalText(amount: Int): String {
        val mainText = "животн"
        return if (amount == 1) "1 ${mainText}е" else "$amount ${mainText}ых"
    }

    private suspend fun loadAnimals(kennelId: Int, animalType: String): MutableList<Animal> =
        withContext(Dispatchers.IO) {
            val retrofit = Common.retrofitService
            val token = "Bearer ${storage.readString(SharedPreferences.TOKEN_KEY, "")}"
                val response = retrofit.getAnimalsByKennelIdAndAnimalType(
                    token, kennelId, animalType
                )
                if (response.isSuccessful) {
                    response.body() ?: mutableListOf()
                } else {
                    when (response.code()) {
                        403 -> goToLogin()
                        else -> mutableListOf<Animal>()
                    }
                    mutableListOf()
                }
        }

    private fun goToLogin() {
        Log.i("info", "Токен протух, необходимо его обновить")
        TODO()
    }

    private fun getAnimalCountText(amount: Int): String {
        val pet = "питом"
        return when(amount % 10) {
            1 -> "$amount ${pet}ец"
            2, 3, 4 -> "$amount ${pet}ца"
            else -> "$amount ${pet}цев"
        }
    }

    private fun showKennelIsNotValidDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)
            .setView(R.layout.not_valid_kennel_dialog)
            .setBackground(ColorDrawable(requireContext().getColor((R.color.transparent))))
            .show()
        val okBtn: Button? = dialog.findViewById(R.id.not_valid_kennel_dialog_ok_btn)
        okBtn?.setOnClickListener {
            dialog.cancel()
        }

    }



}