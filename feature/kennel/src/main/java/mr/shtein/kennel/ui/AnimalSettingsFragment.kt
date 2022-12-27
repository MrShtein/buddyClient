package mr.shtein.kennel.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.*
import mr.shtein.data.exception.BadTokenException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.Animal
import mr.shtein.data.repository.AnimalRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.ui_util.AnimalPhotoAdapter
import mr.shtein.ui_util.OnSnapPositionChangeListener
import mr.shtein.ui_util.SnapOnScrollListener
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.setStatusBarColor
import org.koin.android.ext.android.inject
import java.net.ConnectException
import java.net.SocketTimeoutException

private const val ANIMAL_KEY = "animal_key"
private const val FROM_ADD_ANIMAL_REQUEST_KEY = "from_add_animal_request_key"
private const val RESULT_LISTENER_BUNDLE_KEY = "message_from_animal_card"
private const val DELETE_ANIMAL_MSG = "Питомец успешно удален"
private const val BUNDLE_KEY_FOR_ANIMAL_OBJECT = "animal_key"
private const val RESULT_LISTENER_KEY = "result_key"

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
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val networkAnimalRepository: AnimalRepository by inject()
    private val networkImageLoader: ImageLoader by inject()
    private val navigator: KennelNavigation by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        animal = arguments?.getParcelable(ANIMAL_KEY)
        setFragmentResultListener(FROM_ADD_ANIMAL_REQUEST_KEY) { _, bundle ->
            animal = bundle.getParcelable(BUNDLE_KEY_FOR_ANIMAL_OBJECT)
        }

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarColor(false)
        initViews(view)
        setDataToViews()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        animal?.let { animal ->
            name.text = animal.name
            breed.text = animal.breed
            age.text = animal.getAge()
            color.text = animal.characteristics["color"]
            gender.text = animal.gender
            description.text = animal.description
            adapter.animalPhotos = animal.getImgUrls()
            adapter.notifyDataSetChanged()
        }

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
        adapter = AnimalPhotoAdapter(animalPhotoInString ?: listOf(), networkImageLoader)
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
            animal?.let {
                navigator.moveToAddAnimal(animal = it, isFromSettings = true)
            }
        }
    }

    private fun buildAndShowDeleteAnimalDialog() {
        val token = userPropertiesRepository.getUserToken()

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)
            .setView(R.layout.animal_delete_dialog)
            //.setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
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
                        networkAnimalRepository.deleteAnimal(token, animal.id)
                        val bundle = bundleOf(
                            RESULT_LISTENER_BUNDLE_KEY to DELETE_ANIMAL_MSG,
                            ANIMAL_KEY to animal.id,
                        )
                        setFragmentResult(RESULT_LISTENER_KEY, bundle)
                        spinner?.isVisible = false
                        dialog.dismiss()
                        navigator.backToPreviousFragment()
                    }
                } catch (ex: BadTokenException) {
                    userPropertiesRepository.saveUserToken("")
                    val errorText = getString(R.string.bad_token_msg)
                    showError(errorText = errorText)
                    if (motionLayout is MotionLayout) motionLayout.transitionToEnd()
                } catch (ex: ServerErrorException) {
                    val errorText = getString(R.string.server_unavailable_msg)
                    showError(errorText = errorText)
                    if (motionLayout is MotionLayout) motionLayout.transitionToEnd()
                } catch (ex: ConnectException) {
                    val errorText = getString(R.string.internet_failure_text)
                    showError(errorText = errorText)
                    if (motionLayout is MotionLayout) motionLayout.transitionToEnd()
                } catch (ex: SocketTimeoutException) {
                    val errorText = getString(R.string.internet_failure_text)
                    showError(errorText = errorText)
                    if (motionLayout is MotionLayout) motionLayout.transitionToEnd()
                } finally {
                    dialog.dismiss()
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

    private fun showError(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
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
}
