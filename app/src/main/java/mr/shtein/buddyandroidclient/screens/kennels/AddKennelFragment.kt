package mr.shtein.buddyandroidclient.screens.kennels

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.utils.BottomSheetDialogShower
import mr.shtein.buddyandroidclient.utils.SharedPreferences

class AddKennelFragment : Fragment(R.layout.add_kennel_fragment) {

    private lateinit var kennelsBtn: MaterialButton
    private lateinit var volunteersBtn: MaterialButton
    private lateinit var kennelsUnderscore: View
    private lateinit var volunteersUnderscore: View
    private lateinit var descriptionView: TextView
    private lateinit var storage: SharedPreferences
    private lateinit var addKennelBtn: MaterialButton


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setListeners()
    }

    private fun initViews(view: View) {
        storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        kennelsBtn = view.findViewById(R.id.add_kennel_fragment_kennels_btn)
        volunteersBtn = view.findViewById(R.id.add_kennel_fragment_volunteers_btn)
        kennelsUnderscore = view.findViewById(R.id.add_kennel_fragment_kennel_underscore)
        volunteersUnderscore = view.findViewById(R.id.add_kennel_fragment_volunteer_underscore)
        descriptionView = view.findViewById(R.id.add_kennel_fragment_kennels_or_volunteers_absence)
        addKennelBtn = view.findViewById(R.id.add_kennel_fragment_add_btn)
    }

    private fun setListeners() {
        kennelsBtn.setOnClickListener {
            val volunteersUnderscoreVisibility = volunteersUnderscore.isVisible
            if (volunteersUnderscoreVisibility) {
                volunteersUnderscore.isVisible = false
                kennelsUnderscore.isVisible = true
            }
            val kennelsInfo = storage.readString(SharedPreferences.USER_KENNELS_KEY, "")
            if (kennelsInfo == "") descriptionView.text =
                requireContext().getText(R.string.add_kennel_fragment_empty_kennels_text)
        }

        volunteersBtn.setOnClickListener {
            val kennelsUnderscoreVisibility = kennelsUnderscore.isVisible
            if (kennelsUnderscoreVisibility) {
                kennelsUnderscore.isVisible = false
                volunteersUnderscore.isVisible = true
            }
            val kennelsInfo = storage.readString(SharedPreferences.USER_VOLUNTEERS_KEY, "")
            if (kennelsInfo == "") descriptionView.text =
                requireContext().getText(R.string.add_kennel_fragment_empty_volunteers_text)
        }

        addKennelBtn.setOnClickListener {
            val token = storage.readString(SharedPreferences.TOKEN_KEY, "")
            if (token == "") {
                BottomSheetDialogShower.createAndShowBottomSheetDialog(requireView(), this)
            } else {
                findNavController().navigate(R.id.action_addKennelFragment_to_kennelSettingsFragment)
            }
        }
    }
}