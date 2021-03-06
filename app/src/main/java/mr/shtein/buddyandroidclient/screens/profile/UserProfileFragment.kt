package mr.shtein.buddyandroidclient.screens.profile

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.*
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.setInsetsListenerForPadding
import mr.shtein.buddyandroidclient.setStatusBarColor
import mr.shtein.buddyandroidclient.utils.SharedPreferences


class UserProfileFragment : Fragment(R.layout.user_profile_fragment) {

    private var personAvatarImg: ImageView? = null
    private var personName: TextView? = null
    private var personStatus: TextView? = null
    private var personSettingsBtn: ImageButton? = null
    private lateinit var exitBtn: MaterialButton
    private lateinit var storage: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = SharedPreferences(requireContext(),
            SharedPreferences.PERSISTENT_STORAGE_NAME)
        setStatusBarColor(true)
        setInsetsListenerForPadding(view, left = false, top = true, right = false, bottom = false)
        initViews(view)
        setImageToAvatar(storage)
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
            findNavController().navigate(R.id.userProfileFragment_to_userSettingsFragment)
        }
        exitBtn.setOnClickListener {
            storage.cleanAllData()
            findNavController().navigate(R.id.action_userProfileFragment2_to_cityChoiceFragmentForUserSettings)
        }
    }

    private fun setTextToViews() {
        val storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        val name = storage.readString(SharedPreferences.USER_NAME_KEY, "")
        val status = storage.readString(SharedPreferences.USER_ROLE_KEY, "")

        personName?.text = name
        personStatus?.text =
            when (status) {
                "ROLE_USER" -> "????????????????????????"
                "ROLE_ADMIN" -> "??????????????????????????"
                "ROLE_VOLUNTEER" -> "????????????????"
                else -> ""
            }
    }

    private fun setImageToAvatar(storage: SharedPreferences) {
        val imageUri = storage.readString(SharedPreferences.USER_AVATAR_URI_KEY, "")
        if (imageUri == "") {
            personAvatarImg?.setImageResource(R.drawable.user_photo_placeholder)
        } else {
            personAvatarImg?.setImageURI(Uri.parse(imageUri))
        }
    }


}