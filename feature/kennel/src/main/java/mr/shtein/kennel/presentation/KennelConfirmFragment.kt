package mr.shtein.kennel.presentation

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mr.shtein.data.exception.ItemAlreadyExistException
import mr.shtein.data.exception.ServerErrorException
import mr.shtein.data.model.AvatarWrapper
import mr.shtein.data.model.KennelRequest
import mr.shtein.data.repository.KennelPropertiesRepository
import mr.shtein.data.repository.KennelRepository
import mr.shtein.data.repository.UserPropertiesRepository
import mr.shtein.kennel.R
import mr.shtein.kennel.navigation.KennelNavigation
import mr.shtein.ui_util.setInsetsListenerForPadding
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileNotFoundException
import java.lang.StringBuilder
import java.net.ConnectException
import java.net.SocketTimeoutException

class KennelConfirmFragment : Fragment(R.layout.kennel_confirm_fragment) {

    companion object {
        private const val SETTINGS_DATA_KEY = "settings_data"
        private const val KENNEL_AVATAR_FILE_NAME = "kennel_avatar"
        private const val ADMIN_ROLE_TXT = "ROLE_ADMIN"
    }

    private lateinit var kennelRequest: KennelRequest
    private lateinit var avatarImg: ImageView
    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var email: TextView
    private lateinit var cityAndRegion: TextView
    private lateinit var street: TextView
    private lateinit var identificationNum: TextView
    private lateinit var saveBtn: MaterialButton
    private lateinit var progressBar: ProgressBar
    private var coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private val kennelPropertiesRepository: KennelPropertiesRepository by inject()
    private val networkKennelRepository: KennelRepository by inject()
    private val userPropertiesRepository: UserPropertiesRepository by inject()
    private val navigator: KennelNavigation by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: bundleOf()
        kennelRequest = bundle.getParcelable(SETTINGS_DATA_KEY)!!

        setInsetsListenerForPadding(
            view = view,
            left = false,
            top = true,
            right = false,
            bottom = false
        )
        initViews(view)
        setViews()
        setListeners()
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
        name.text = kennelRequest.kennelName
        phone.text = kennelRequest.kennelPhoneNum
        email.text = kennelRequest.kennelEmail
        cityAndRegion.text = makeAndSetCityAndRegion()
        street.text = makeAndSetStreetHouseAndBuilding()
        identificationNum.text = kennelRequest.kennelIdentifyNum.toString()
    }

    private fun setAvatar() {
        val avatarUri = kennelRequest.kennelAvtUri
        if (avatarUri != "") {
            avatarImg.setImageURI(Uri.parse(avatarUri))
        }
    }

    private fun makeAndSetCityAndRegion(): String {
        val cityAndRegion = kennelRequest.kennelCity.split(",")
        return cityAndRegion[1]
    }

    private fun makeAndSetStreetHouseAndBuilding(): String {
        val strBuilder = StringBuilder()
        strBuilder.append(kennelRequest.kennelStreet)
            .append(" д.")
            .append(kennelRequest.kennelHouseNum)
        if (kennelRequest.kennelBuildingNum != "") {
            strBuilder.append(" корп. ${kennelRequest.kennelBuildingNum}")
        }
        return strBuilder.toString()
    }

    private fun setListeners() {
        saveBtn.setOnClickListener {
            saveBtn.isEnabled = false
            progressBar.isVisible = true

            coroutineScope.launch {
                try {
                    val avatarWrapper: AvatarWrapper? = getPhotoAndType()
                    addNewKennel(avatarWrapper)
                    avatarWrapper?.file?.delete()
                    userPropertiesRepository.saveUserRole(ADMIN_ROLE_TXT)
                    showDialog(true)

                } catch (ex: ItemAlreadyExistException) {
                    showDialog(false)
                } catch (ex: FileNotFoundException) {
                    Log.e(
                        "error",
                        "Не получилось найти файл"
                    )
                } catch (ex: SocketTimeoutException) {
                    val exText = requireContext().getString(R.string.internet_failure_text)
                    progressBar.isVisible = false
                    showError(errorText = exText)
                    navigator.backToPreviousFragment()
                } catch (ex: ConnectException) {
                    val exText = requireContext().getString(R.string.internet_failure_text)
                    progressBar.isVisible = false
                    showError(errorText = exText)
                    navigator.backToPreviousFragment()
                } catch (ex: ServerErrorException) {
                    val exText = requireContext().getString(R.string.server_unavailable_msg)
                    progressBar.isVisible = false
                    showError(errorText = exText)
                    navigator.backToPreviousFragment()
                }

            }
        }
    }

    private suspend fun addNewKennel(avatarWrapper: AvatarWrapper?) {
        val token = userPropertiesRepository.getUserToken()
        kennelRequest.userId = userPropertiesRepository.getUserId()
        networkKennelRepository.addNewKennel(
            token = token,
            kennelRequest = kennelRequest,
            avatarWrapper = avatarWrapper
        )
    }

    private suspend fun getPhotoAndType(): AvatarWrapper? {
        val avatarStr = kennelRequest.kennelAvtUri
        return if (avatarStr != "") {
            val avatarUri = Uri.parse(avatarStr)
            val file = File(requireContext().filesDir, KENNEL_AVATAR_FILE_NAME)
            val imgStream = requireContext().contentResolver.openInputStream(avatarUri)
            val imgType = requireContext().contentResolver.getType(avatarUri)
            if (imgStream == null || imgType == null) throw FileNotFoundException()
            file.writeBytes(imgStream.readBytes())
            AvatarWrapper(file, imgType)
        } else {
            null;
        }

    }

    private fun showDialog(isKennelAlreadyExist: Boolean) {

        if (!isKennelAlreadyExist) {
            val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)
                .setView(R.layout.kennel_failed_dialog)
                //.setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
                .show()
            val okBtn: Button? = dialog.findViewById(R.id.kennel_failed_ok_btn)
            okBtn?.setOnClickListener {
                dialog.dismiss()
                navigator.backToPreviousFragment()
            }
            return
        }

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)
            .setView(R.layout.kennel_confirm_dialog)
            //.setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
            .show()

        val okBtn: Button? = dialog.findViewById(R.id.kennel_confirm_dialog_ok_btn)


        okBtn?.setOnClickListener {
            dialog.dismiss()
            kennelPropertiesRepository.saveKennelAvatarUri("")
            navigator.moveToAddAnimalFromKennelConfirm()
        }
    }

    private fun showError(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }


}