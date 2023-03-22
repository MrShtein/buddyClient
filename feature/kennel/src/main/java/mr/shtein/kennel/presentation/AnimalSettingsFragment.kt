package mr.shtein.kennel.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.data.model.Animal
import mr.shtein.data.repository.ANIMAL_TYPE_KEY
import mr.shtein.kennel.R
import mr.shtein.kennel.databinding.AnimalSettingsFragmentBinding
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.kennel.presentation.state.delete_animal.DeleteAnimalState
import mr.shtein.kennel.presentation.viewmodel.AnimalSettingsViewModel
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


private const val ANIMAL_KEY = "animal_key"
private const val ANIMAL_ID_KEY = "animal_id"
private const val FROM_ADD_ANIMAL_REQUEST_KEY = "from_add_animal_request_key"
private const val RESULT_LISTENER_BUNDLE_KEY = "message_from_animal_card"
private const val DELETE_ANIMAL_MSG = "Питомец успешно удален"
private const val BUNDLE_KEY_FOR_ANIMAL_OBJECT = "animal_key"
private const val RESULT_LISTENER_KEY = "result_key"
private const val BINDING_IS_NULL = "AnimalSettingsFragmentBinding = null"

class AnimalSettingsFragment : Fragment(),
    OnSnapPositionChangeListener {
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: AnimalPhotoAdapter
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var anima: Animal

    //private var animal: Animal? = null
    private val networkImageLoader: ImageLoader by inject()

    //private val navigator: KennelNavigation by inject()
    private val animalSettingsViewModel: AnimalSettingsViewModel by viewModel {
        parametersOf(requireArguments().getLong(ANIMAL_ID_KEY))
    }

    private var _binding: AnimalSettingsFragmentBinding? = null
    private val binding: AnimalSettingsFragmentBinding
        get() = _binding ?: throw RuntimeException(BINDING_IS_NULL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(FROM_ADD_ANIMAL_REQUEST_KEY) { _, bundle ->
            val updatedAnimal = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(BUNDLE_KEY_FOR_ANIMAL_OBJECT, Animal::class.java) as Animal
            } else {
                @Suppress("deprecation")
                bundle.getParcelable(BUNDLE_KEY_FOR_ANIMAL_OBJECT)
                    ?: animalSettingsViewModel.selectedAnimal.value
            }
            if (updatedAnimal != null) {
                animalSettingsViewModel.onAnimalUpdated(updatedAnimal = updatedAnimal)
            }
        }

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AnimalSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animalSettingsViewModel.selectedAnimal.observe(viewLifecycleOwner) { animal ->
            with(binding) {

                val animalPhotoInString = animal?.imgUrl?.map {
                    it.url
                }
                adapter = AnimalPhotoAdapter(animalPhotoInString ?: listOf(), networkImageLoader)
                photoRecyclerView.adapter = adapter

                animalSettingsName.text = animal.name
                animalSettingsBreed.text = animal.breed
                animalSettingsAge.text = animal.getAge()
                animalSettingsColor.text = animal.characteristics["color"]
                animalSettingsGender.text = animal.gender
                animalSettingsDescription.text = animal.description
                adapter.animalPhotos = animal.getImgUrls()
                adapter.notifyDataSetChanged()
                binding.animalSettingsPhotoCounter.text =
                    getString(
                        R.string.big_card_animal_photo_counter,
                        1,
                        animal?.imgUrl?.size ?: 1
                    )


            }
            Log.d("test", "Animal   ${animal.imgUrl.size}")
        }
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        animalSettingsViewModel.dialogState.observe(viewLifecycleOwner) { isDialogVisible ->
            if (isDialogVisible) buildAndShowDeleteAnimalDialog()
        }

        setInsetsListenerForPadding(
            view = view,
            left = false,
            top = false,
            right = false,
            bottom = true
        )
        setStatusBarColor(false)
        setDataToViews()
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setDataToViews() {
        photoRecyclerView = binding.animalSettingsPhotoContainer
        photoRecyclerView.setHasFixedSize(true)
        photoRecyclerView.layoutManager = layoutManager
        val snapHelper = PagerSnapHelper()
        val snapOnScrollListener =
            SnapOnScrollListener(snapHelper, this@AnimalSettingsFragment)
        photoRecyclerView.addOnScrollListener(snapOnScrollListener)
        snapHelper.attachToRecyclerView(photoRecyclerView)

    }

    private fun setListeners() {
        binding.animalSettingsDeleteBtn.setOnClickListener {
            animalSettingsViewModel.onDeleteDialogShowBtnClick()
        }

        binding.animalSettingsChangeBtn.setOnClickListener {
            animalSettingsViewModel.changeAnimalInfo()

        }
    }

    private fun buildAndShowDeleteAnimalDialog() {

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

            animalSettingsViewModel.deleteAnimal(requireContext(), this@AnimalSettingsFragment)

            animalSettingsViewModel.deleteState.observe(viewLifecycleOwner) {
                spinner?.isVisible = false
                when (it) {
                    is DeleteAnimalState.Loading -> spinner?.isVisible = true
                    is DeleteAnimalState.ErrorInfo -> {
                        showError(it.error)
                        if (motionLayout is MotionLayout) motionLayout.transitionToEnd()
                    }
                    is DeleteAnimalState.Deleted -> {
                        val bundle = bundleOf(
                            RESULT_LISTENER_BUNDLE_KEY to DELETE_ANIMAL_MSG,
                            ANIMAL_KEY to it.animalId
                        )
                        setFragmentResult(RESULT_LISTENER_KEY, bundle)
                        dialog.dismiss()
                    }
                }
            }
            dialog.dismiss()
        }
        negativeBtn?.setOnClickListener {
            dialog.cancel()
        }
        okBtn?.setOnClickListener {//  Зачем эта кнопка ???
            dialog.cancel()
        }
    }

    private fun showError(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }

    override fun onSnapPositionChange(position: Int) {
        val elementsCount = adapter.animalPhotos.size
        binding.animalSettingsPhotoCounter.text =
            getString(
                R.string.big_card_animal_photo_counter,
                position + 1,
                elementsCount
            )

    }
}
