package mr.shtein.profile.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.*
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.MaterialSharedAxis
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.profile.navigation.ProfileNavigation
import mr.shtein.ui_util.FragmentsListForAssigningAnimation
import mr.shtein.ui_util.setInsetsListenerForPadding
import mr.shtein.ui_util.setStatusBarColor
import mr.shtein.user.R
import org.koin.android.ext.android.inject


private const val LAST_FRAGMENT_KEY = "last_fragment"

class UserProfileFragment : Fragment(R.layout.user_profile_fragment) {

    private var personAvatarImg: ImageView? = null
    private var personName: TextView? = null
    private var personStatus: TextView? = null
    private var personSettingsBtn: ImageButton? = null
    private lateinit var exitBtn: MaterialButton
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val navigator: ProfileNavigation by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation? = arguments?.getParcelable(LAST_FRAGMENT_KEY)
        fragmentsListForAssigningAnimation?.let {
            changeAnimations(it)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarColor(true)
        setInsetsListenerForPadding(view, left = false, top = true, right = false, bottom = false)
        initViews(view)
        setImageToAvatar()
        setTextToViews()
        setListeners()
    }

    private fun initViews(view: View) {
        exitBtn = view.findViewById(R.id.user_profile_exit_btn)
        personAvatarImg = view.findViewById(R.id.profile_avatar_img)
        personName = view.findViewById(R.id.profile_name_text)
        personStatus = view.findViewById(R.id.profile_status_text)
        personSettingsBtn = view.findViewById(R.id.profile_settings_btn)
    }

    private fun setListeners() {
        personSettingsBtn?.setOnClickListener {
            navigator.moveToUserSettings()

        }
        exitBtn.setOnClickListener {
            userPropertiesRepository.removeAll()
            navigator.moveToCityChoiceFromUserProfile()

        }
    }

    private fun setTextToViews() {
        val name = userPropertiesRepository.getUserName()
        val status = userPropertiesRepository.getUserRole()

        personName?.text = name
        personStatus?.text =
            when (status) {
                "ROLE_USER" -> "Пользователь"
                "ROLE_ADMIN" -> "Администратор"
                "ROLE_VOLUNTEER" -> "Волонтер"
                else -> ""
            }
    }

    private fun setImageToAvatar() {
        val imageUri = userPropertiesRepository.getUserUri()
        if (imageUri == "") {
            personAvatarImg?.setImageResource(R.drawable.light_person_placeholder)
        } else {
            personAvatarImg?.setImageURI(Uri.parse(imageUri))
        }
    }

    private fun changeAnimations(fragmentsListForAssigningAnimation: FragmentsListForAssigningAnimation) {
        when (fragmentsListForAssigningAnimation) {
            FragmentsListForAssigningAnimation.ANIMAL_LIST -> {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            }
            else -> {}
        }
    }


}