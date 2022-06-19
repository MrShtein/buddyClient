package mr.shtein.buddyandroidclient.screens.kennels

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.*
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.adapters.AnimalPhotoAdapter
import mr.shtein.buddyandroidclient.model.Animal
import mr.shtein.buddyandroidclient.retrofit.Common
import mr.shtein.buddyandroidclient.setStatusBarColor
import mr.shtein.buddyandroidclient.utils.OnSnapPositionChangeListener
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import mr.shtein.buddyandroidclient.utils.event.SnapOnScrollListener
import java.lang.Exception

private const val ANIMAL_KEY = "animal_key"
private const val RESULT_LISTENER_KEY = "result_key"
private const val RESULT_LISTENER_BUNDLE_KEY = "message_from_animal_card"
private const val DELETE_ANIMAL_MSG = "Питомец успешно удален"
private const val FROM_SETTINGS_FRAGMENT_KEY = "I'm from settings"

class AnimalSettingsFragment : Fragment(R.layout.animal_settings_fragment),
    OnSnapPositionChangeListener {
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: AnimalPhotoAdapter
    private lateinit var name: TextView
    private lateinit var breed: TextView
    private lateinit var age: TextView
    private lateinit var color: TextView
    private lateinit var gender: TextView
    private lateinit var description: TextView
    private lateinit var changeBtn: MaterialButton
    private lateinit var deleteBtn: MaterialButton
    private lateinit var photoCounter: TextView
    private val coroutine: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var animal: Animal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onStop() {
        super.onStop()
        animal = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animal = arguments?.getParcelable(ANIMAL_KEY)
        setStatusBarColor(false)
        initViews(view)
        setDataToViews()
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutine.cancel()
    }

    private fun initViews(view: View) {
        photoRecyclerView = view.findViewById(R.id.animal_settings_photo_container)
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        name = view.findViewById(R.id.animal_settings_name)
        breed = view.findViewById(R.id.animal_settings_breed)
        age = view.findViewById(R.id.animal_settings_age)
        color = view.findViewById(R.id.animal_settings_color)
        gender = view.findViewById(R.id.animal_settings_gender)
        description = view.findViewById(R.id.animal_settings_description)
        changeBtn = view.findViewById(R.id.animal_settings_change_btn)
        deleteBtn = view.findViewById(R.id.animal_settings_delete_btn)
        photoCounter = view.findViewById(R.id.animal_settings_photo_counter)
    }

    private fun setDataToViews() {
        photoRecyclerView.setHasFixedSize(true)
        photoRecyclerView.layoutManager = layoutManager
        val snapHelper = PagerSnapHelper()
        val snapOnScrollListener =
            SnapOnScrollListener(snapHelper, this@AnimalSettingsFragment)
        photoRecyclerView.addOnScrollListener(snapOnScrollListener)
        snapHelper.attachToRecyclerView(photoRecyclerView)
        val animalPhotoInString = animal?.imgUrl?.map {
            it.url
        }
        adapter = AnimalPhotoAdapter(animalPhotoInString ?: listOf())
        photoRecyclerView.adapter = adapter

        photoCounter.text =
            getString(
                R.string.big_card_animal_photo_counter,
                1,
                animal?.imgUrl?.size ?: 1
            )

        animal?.let { animal ->
            name.text = animal.name
            breed.text = animal.breed
            age.text = animal.getAge()
            color.text = animal.characteristics["color"]
            gender.text = animal.gender
            description.text = animal.description
        }


    }

    private fun setListeners() {
        deleteBtn.setOnClickListener {
            buildAndShowDeleteAnimalDialog()
            Log.d("dialog", "Dialog was build")
        }

        changeBtn.setOnClickListener {
            val bundle = bundleOf(
                ANIMAL_KEY to animal,
                FROM_SETTINGS_FRAGMENT_KEY to true
            )
            findNavController().navigate(
                R.id.action_animalSettingsFragment_to_addAnimalFragment,
                bundle
            )
        }
    }

    private fun buildAndShowDeleteAnimalDialog() {
        val storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        val token = storage.readString(SharedPreferences.TOKEN_KEY, "")

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)
            .setView(R.layout.animal_delete_dialog)
            .setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
            .show()

        val positiveBtn: Button? =
            dialog?.findViewById(R.id.animal_delete_dialog_positive_btn)
        val negativeBtn: Button? = dialog?.findViewById(R.id.animal_delete_dialog_negative_btn)
        val okBtn: Button? = dialog?.findViewById(R.id.animal_delete_dialog_ok_btn)
        val spinner: Spinner? = dialog?.findViewById(R.id.animal_delete_dialog_spinner)
        val motionLayout: MotionLayout? =
            dialog?.findViewById(R.id.animal_delete_dialog_motion_layout)

        positiveBtn?.setOnClickListener {
            spinner?.isVisible = true
            coroutine.launch {
                try {
                    animal?.let { animal ->
                        val result = deleteAnimal(animal.id, token)
                        spinner?.isVisible = false
                        if (result) {
                            dialog.cancel()
                            findNavController().popBackStack()
                        }
                    }
                } catch (ex: Exception) {
                    Log.d("error", ex.message.toString())
                    if (motionLayout is MotionLayout) motionLayout.transitionToEnd()
                }
            }
        }

        negativeBtn?.setOnClickListener {
            dialog.cancel()
        }

        okBtn?.setOnClickListener {
            dialog.cancel()
        }
    }

    override fun onSnapPositionChange(position: Int) {
        val elementsCount = animal?.imgUrl?.size
        photoCounter.text =
            getString(
                R.string.big_card_animal_photo_counter,
                position + 1,
                elementsCount
            )
    }

    private suspend fun deleteAnimal(animalId: Long, token: String): Boolean = withContext(Dispatchers.IO) {
            val retrofit = Common.retrofitService
            val response = retrofit.deleteAnimal(token, animalId)
            val bundle = bundleOf(
                RESULT_LISTENER_BUNDLE_KEY to DELETE_ANIMAL_MSG,
                ANIMAL_KEY to animal?.id,
            )
            when (response.code()) {
                200 -> {
                    setFragmentResult(RESULT_LISTENER_KEY, bundle)
                    return@withContext true
                }
                403 -> {
                    throw Exception("Что-то пошло не так c токеном") //TODO Разобраться с ошибками
                }
                else -> throw Exception("Что-то пошло не так") //TODO Разобраться с ошибками
            }
        }


}
