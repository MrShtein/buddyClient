package mr.shtein.buddyandroidclient.screens.profile

import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.*
import android.widget.*
import androidx.navigation.fragment.findNavController
import mr.shtein.buddyandroidclient.R


class UserProfileFragment : Fragment(R.layout.user_profile_fragment) {

    private var profileAvatarImg: ImageView? = null
    private var profileName: TextView? = null
    private var profileStatus: TextView? = null
    private var profileSettingsBtn: ImageButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setListeners()
    }

    private fun initViews(view: View) {
        profileAvatarImg = view.findViewById(R.id.profile_avatar_img)
        profileName = view.findViewById(R.id.profile_name_text)
        profileStatus = view.findViewById(R.id.profile_status_text)
        profileSettingsBtn = view.findViewById(R.id.profile_settings_btn)
    }

    private fun setListeners() {
        profileSettingsBtn?.setOnClickListener {
            findNavController().navigate(R.id.userProfileFragment_to_userSettingsFragment)
        }
    }


}