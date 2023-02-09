package mr.shtein.animal.presentation.screen

import android.Manifest
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mr.shtein.animal.R
import mr.shtein.data.exception.BidExistException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.Animal
import mr.shtein.data.model.Kennel
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.data.repository.UserRepository
import mr.shtein.network.ImageLoader
import mr.shtein.ui_util.AnimalPhotoAdapter
import mr.shtein.ui_util.OnSnapPositionChangeListener
import mr.shtein.ui_util.SnapOnScrollListener
import mr.shtein.ui_util.setStatusBarColor
import org.koin.android.ext.android.inject
import java.io.IOException


class AnimalsCardFragment : Fragment(), OnSnapPositionChangeListener {

    private lateinit var adapter: AnimalPhotoAdapter
    private lateinit var animalRecyclerView: RecyclerView
    private lateinit var currentView: View
    private lateinit var intentForCall: Intent
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var imageCount: TextView
    private lateinit var animalName: TextView
    private lateinit var gender: TextView
    private lateinit var age: TextView
    private lateinit var breed: TextView
    private lateinit var color: TextView
    private lateinit var description: TextView
    private lateinit var kennelName: TextView
    private lateinit var address: TextView
    private lateinit var avatar: ShapeableImageView
    private lateinit var distance: TextView
//    private lateinit var heartBox: CheckBox
    private lateinit var writeBtn: MaterialButton
    private lateinit var callBtn: MaterialButton
    private lateinit var becomeVolunteerBtn: MaterialButton
    private lateinit var constraintLayout: ConstraintLayout
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val userRepository: UserRepository by inject()
    private val networkImageLoader: ImageLoader by inject()
    private var animal: Animal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStatusBarColor(false)
        animal = arguments?.getParcelable(ANIMAL_KEY)
        val view = inflater.inflate(R.layout.animal_card_fragment, container, false)



        initViews(view)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val dip = 40f
            val extraPaddingForConstraint = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                resources.displayMetrics
            )
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            constraintLayout.setPadding(0, 0, 0, insets.bottom + extraPaddingForConstraint.toInt())
            WindowInsetsCompat.CONSUMED
        }
        setValuesToView()
        setListeners()
        currentView = view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { x ->
                if (x) {
                    Log.d("tup", "permission is allowed")
                    context?.startActivity(intentForCall)
                } else {
                    Log.d("tup", "permission is denied")
                }
            }

        super.onViewCreated(view, savedInstanceState)
        animalRecyclerView = view.findViewById(R.id.animal_card_photo_gallery)
        getAnimalPhotos()

    }

    override fun onSnapPositionChange(position: Int) {
        val elementsCount = animalRecyclerView.adapter?.itemCount
        val counter = currentView.findViewById<TextView>(R.id.animal_card_image_count)

        counter.text =
            getString(R.string.big_card_animal_photo_counter, position + 1, elementsCount)
    }


    private fun getAnimalPhotos() {

        animalRecyclerView.setHasFixedSize(true)
        animalRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        val snapOnScrollListener =
            SnapOnScrollListener(snapHelper, this@AnimalsCardFragment)
        animalRecyclerView.addOnScrollListener(snapOnScrollListener)
        snapHelper.attachToRecyclerView(animalRecyclerView)

        animal?.let { animal ->
            val imgUrls = animal.getImgUrls()
            adapter = AnimalPhotoAdapter(imgUrls, networkImageLoader)
            adapter.notifyDataSetChanged()
            animalRecyclerView.adapter = adapter
        }
    }

    private fun initViews(view: View) {
        imageCount = view.findViewById(R.id.animal_card_image_count)
        animalName = view.findViewById(R.id.animal_card_animal_name)
//        heartBox = view.findViewById(R.id.animal_card_heart)
        distance = view.findViewById(R.id.animal_card_distance_text)
        gender = view.findViewById(R.id.animal_card_gender_value)
        age = view.findViewById(R.id.animal_card_age_value)
        breed = view.findViewById(R.id.animal_card_breed_value)
        color = view.findViewById(R.id.animal_card_color_value)
        description = view.findViewById(R.id.animal_card_description)
        kennelName = view.findViewById(R.id.animal_card_kennel_name_value)
        address = view.findViewById(R.id.animal_card_kennel_address)
        avatar = view.findViewById(R.id.animal_card_kennel_avatar)
        writeBtn = view.findViewById(R.id.animal_card_email_btn)
        callBtn = view.findViewById(R.id.animal_card_phone_btn)
        becomeVolunteerBtn = view.findViewById(R.id.animal_card_add_volunteer_btn)
        constraintLayout = view.findViewById(R.id.animal_card_constraint)
    }

    private fun setValuesToView() {
        animal?.let { animal ->
            imageCount.text = getString(
                R.string.big_card_animal_photo_counter,
                1,
                animal.imgUrl.size
            )
            animalName.text = animal.name
            gender.text = animal.gender
            age.text = animal.getAge()
            breed.text = animal.breed
            color.text = animal.characteristics["color"]
            description.text = animal.description
            if (animal.distance != "") {
                distance.visibility = View.VISIBLE
                distance.text = animal.distance
            }

            val kennel: Kennel = animal.kennel
            kennelName.text = kennel.name
            address.text = kennel.address

            val endpoint = resources.getString(R.string.kennel_avatar_endpoint)

            val dogPlaceholder = context?.getDrawable(R.drawable.light_dog_placeholder)!!
            networkImageLoader.setPhotoToView(
                avatar,
                endpoint,
                kennel.avatarUrl,
                dogPlaceholder
            )

        }
    }

    private fun setListeners() {
        animal?.let { animal ->
            val kennel = animal.kennel
            callBtn.setOnClickListener {
                intentForCall = Intent(Intent.ACTION_CALL)
                intentForCall.data =
                    Uri.parse("tel:${kennel.phoneNumber}")

                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    requireContext().startActivity(intentForCall)
                } else {
                    requestPermissionLauncher.launch("android.permission.CALL_PHONE")
                }

            }

            writeBtn.setOnClickListener {
                val emailIntent = Intent(ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(EXTRA_EMAIL, arrayOf(kennel.email))
                }
                startActivity(emailIntent)
            }

            becomeVolunteerBtn.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val token = userPropertiesRepository.getUserToken()
                    try {
                        userRepository.addBidToBecomeVolunteer(token = token, kennelId = kennel.id)
                        val message: String = getString(R.string.become_volunteer_success_message)
                        showMessage(message)
                    } catch (ex: NotFoundException) {
                        val message: String = getString(R.string.kennel_not_found_error)
                        showMessage(message)
                    } catch (ex: BidExistException) {
                        showMessage(ex.message!!)
                    } catch (ex: ServerErrorException) {
                        val message: String = getString(R.string.server_error_msg)
                        showMessage(message)
                    } catch (ex: IOException) {
                        val message: String = getString(R.string.internet_failure_text)
                        showMessage(message)
                    }
                }
            }
        }
    }

    companion object {
        private const val ANIMAL_KEY = "animal_key"
    }

    private fun showMessage(message: String)  {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
        snackBar.show()
        snackBar.setAction("Ok") {
            snackBar.dismiss()
        }
    }
}