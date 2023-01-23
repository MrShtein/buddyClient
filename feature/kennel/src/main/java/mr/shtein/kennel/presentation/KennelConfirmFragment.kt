package mr.shtein.kennel.presentation

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch
import mr.shtein.kennel.R
import mr.shtein.kennel.presentation.state.kennel_confirm.KennelConfirmScreenState
import mr.shtein.kennel.presentation.state.kennel_confirm.NewKennelSendingState
import mr.shtein.kennel.presentation.viewmodel.KennelConfirmViewModel
import mr.shtein.ui_util.setInsetsListenerForPadding
import mr.shtein.ui_util.setStatusBarColor
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.StringBuilder

class KennelConfirmFragment : Fragment(R.layout.kennel_confirm_fragment) {

    companion object {
        private const val SETTINGS_DATA_KEY = "settings_data"
    }

    private lateinit var avatarImg: ImageView
    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var email: TextView
    private lateinit var cityAndRegion: TextView
    private lateinit var street: TextView
    private lateinit var identificationNum: TextView
    private lateinit var saveBtn: MaterialButton
    private lateinit var progressBar: ProgressBar
    private val kennelConfirmViewModel: KennelConfirmViewModel by viewModel {
        parametersOf(arguments!!.getParcelable(SETTINGS_DATA_KEY))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBarColor(isBlack = true)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                kennelConfirmViewModel.kennelConfirmScreenState.collect { kennelConfirmScreenState ->
                    renderState(kennelConfirmScreenState = kennelConfirmScreenState)
                }
            }
        }

        setInsetsListenerForPadding(
            view = view,
            left = false,
            top = true,
            right = false,
            bottom = false
        )
        initViews(view)
        setListeners()
    }

    private fun renderState(kennelConfirmScreenState: KennelConfirmScreenState) {
        when (kennelConfirmScreenState.sendingState) {
            NewKennelSendingState.Sending -> {
                saveBtn.isEnabled = false
                progressBar.isVisible = true
                setViews()
            }
            NewKennelSendingState.Success -> {
                progressBar.isVisible = false
                setViews()
                showDialog(isKennelAlreadyExist = false)
            }
            NewKennelSendingState.Exist -> {
                saveBtn.isEnabled = true
                progressBar.isVisible = false
                setViews()
                showDialog(isKennelAlreadyExist = true)
            }
            is NewKennelSendingState.Failure -> {
                progressBar.isVisible = false
                saveBtn.isEnabled = true
                setViews()
                val textError: String = getString(kennelConfirmScreenState.sendingState.message)
                showError(errorText = textError)
            }
            null -> {
                setViews()
            }
        }
    }

    private fun initViews(view: View) {
        avatarImg = view.findViewById(R.id.kennel_confirm_avatar)
        name = view.findViewById(R.id.kennel_confirm_name)
        phone = view.findViewById(R.id.kennel_confirm_phone)
        email = view.findViewById(R.id.kennel_confirm_email)
        cityAndRegion = view.findViewById(R.id.kennel_confirm_address_city)
        street = view.findViewById(R.id.kennel_confirm_address_street)
        identificationNum = view.findViewById(R.id.kennel_confirm_identification_number)
        saveBtn = view.findViewById(R.id.kennel_confirm_save_btn)
        progressBar = view.findViewById(R.id.kennel_confirm_progress_bar)
    }

    private fun setViews() {
        setAvatar()
        name.text = kennelConfirmViewModel.kennelRequest.kennelName
        phone.text = kennelConfirmViewModel.kennelRequest.kennelPhoneNum
        email.text = kennelConfirmViewModel.kennelRequest.kennelEmail
        cityAndRegion.text = makeAndSetCityAndRegion()
        street.text = makeAndSetStreetHouseAndBuilding()
        identificationNum.text = kennelConfirmViewModel.kennelRequest.kennelIdentifyNum.toString()
    }

    private fun setAvatar() {
        val avatarUri = kennelConfirmViewModel.kennelRequest.kennelAvtUri
        if (avatarUri != "") {
            avatarImg.setImageURI(Uri.parse(avatarUri))
        }
    }

    private fun makeAndSetCityAndRegion(): String {
        val cityAndRegion = kennelConfirmViewModel.kennelRequest.kennelCity.split(",")
        return cityAndRegion[1]
    }

    private fun makeAndSetStreetHouseAndBuilding(): String {
        val strBuilder = StringBuilder()
        strBuilder.append(kennelConfirmViewModel.kennelRequest.kennelStreet)
            .append(" д.")
            .append(kennelConfirmViewModel.kennelRequest.kennelHouseNum)
        if (kennelConfirmViewModel.kennelRequest.kennelBuildingNum != "") {
            strBuilder.append(" корп. ${kennelConfirmViewModel.kennelRequest.kennelBuildingNum}")
        }
        return strBuilder.toString()
    }

    private fun setListeners() {
        saveBtn.setOnClickListener {
            val avatarUri: String = kennelConfirmViewModel.kennelRequest.kennelAvtUri
            kennelConfirmViewModel.onSaveBtnClick(avatarUri)
        }
    }

    private fun showDialog(isKennelAlreadyExist: Boolean) {

        if (isKennelAlreadyExist) {
            val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)
                .setView(R.layout.kennel_failed_dialog)
                .show()
            val okBtn: Button? = dialog.findViewById(R.id.kennel_failed_ok_btn)
            okBtn?.setOnClickListener {
                dialog.dismiss()
                kennelConfirmViewModel.onExistDialogBtnClick()
            }
            return
        }

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)
            .setView(R.layout.kennel_confirm_dialog)
            .show()

        val okBtn: Button? = dialog.findViewById(R.id.kennel_confirm_dialog_ok_btn)


        okBtn?.setOnClickListener {
            dialog.dismiss()
            kennelConfirmViewModel.onSuccessDialogBtnClick()
        }
    }

    private fun showError(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }


}