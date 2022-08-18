package mr.shtein.buddyandroidclient.screens.kennels

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.data.repository.KennelPropertiesRepository
import mr.shtein.buddyandroidclient.data.repository.UserPropertiesRepository
import mr.shtein.buddyandroidclient.model.AvatarWrapper
import mr.shtein.buddyandroidclient.model.KennelRequest
import mr.shtein.buddyandroidclient.retrofit.NetworkService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.lang.StringBuilder
import java.net.SocketTimeoutException

class KennelConfirmFragment : Fragment(R.layout.kennel_confirm_fragment) {

    companion object {
        private const val SETTINGS_DATA_KEY = "settings_data"
        private const val KENNEL_AVATAR_FILE_NAME = "kennel_avatar"
        private const val ADMIN_ROLE_TXT = "ROLE_ADMIN"
    }

    private lateinit var settingsData: KennelRequest
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
    private val networkService: NetworkService by inject()
    private val kennelPropertiesRepository: KennelPropertiesRepository by inject()
    private val userPropertiesRepository: UserPropertiesRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: bundleOf()
        settingsData = Gson()
            .fromJson(bundle.getString(SETTINGS_DATA_KEY), KennelRequest::class.java)

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
        name.text = settingsData.kennelName
        phone.text = settingsData.kennelPhoneNum
        email.text = settingsData.kennelEmail
        cityAndRegion.text = makeAndSetCityAndRegion()
        street.text = makeAndSetStreetHouseAndBuilding()
        identificationNum.text = settingsData.kennelIdentifyNum.toString()
    }

    private fun setAvatar() {
        val avatarUri = settingsData.kennelAvtUri
        if (avatarUri != "") {
            avatarImg.setImageURI(Uri.parse(avatarUri))
        }
    }

    private fun makeAndSetCityAndRegion(): String {
        val cityAndRegion = settingsData.kennelCity.split(",")
        return cityAndRegion[1]
    }

    private fun makeAndSetStreetHouseAndBuilding(): String {
        val strBuilder = StringBuilder()
        strBuilder.append(settingsData.kennelStreet)
            .append(" д.")
            .append(settingsData.kennelHouseNum)
        if (settingsData.kennelBuildingNum != "") {
            strBuilder.append(" корп. ${settingsData.kennelBuildingNum}")
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
                    val response = addNewKennel(avatarWrapper)
                    when (response.code()) {
                        201 -> {
                            userPropertiesRepository.saveUserRole(ADMIN_ROLE_TXT)
                            avatarWrapper?.file?.delete()
                            showDialog(true)
                        }
                        409 -> {
                            avatarWrapper?.file?.delete()
                            showDialog(false)
                        }
                    }

                } catch (ex: FileNotFoundException) {
                    Log.e(
                        "error",
                        "Не получилось найти файл"
                    ) //TODO Найти другие exceptions, которые могут сработать
                } catch (ex: SocketTimeoutException) {
                    Log.e(
                        "error",
                        "Что-то пошло не так и сервер не ответил"
                    )
                }

            }
        }
    }

    private suspend fun addNewKennel(avatarWrapper: AvatarWrapper?): Response<Void> {
        var requestFile: RequestBody? = null
        var body: MultipartBody.Part? = null

        val token = userPropertiesRepository.getUserToken()
        val headers = mutableMapOf<String, String>()
        headers["Authorization"] = token

        settingsData.userId = userPropertiesRepository.getUserId()
        val kennelSettings = Gson().toJson(settingsData)
        val requestBody = RequestBody.create(
            MultipartBody.FORM, kennelSettings
        )

        return if (avatarWrapper != null) {
            requestFile = RequestBody.create(
                MediaType.parse(avatarWrapper.fileType),
                avatarWrapper.file
            )

            body = MultipartBody.Part.createFormData(
                KENNEL_AVATAR_FILE_NAME,
                avatarWrapper.file.name,
                requestFile
            )
            networkService.addNewKennel(headers, requestBody, body)
        } else {
            networkService.addNewKennel(headers, requestBody, null)
        }


    }

    private suspend fun getPhotoAndType(): AvatarWrapper? {
        val avatarStr = settingsData.kennelAvtUri
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
                .setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
                .show()

            val okBtn: Button? = dialog.findViewById(R.id.kennel_failed_ok_btn)
            okBtn?.setOnClickListener {
                dialog.dismiss()
                findNavController().popBackStack()
            }
            return
        }

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)

            .setView(R.layout.kennel_confirm_dialog)
            .setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
            .show()

        val okBtn: Button? = dialog.findViewById(R.id.kennel_confirm_dialog_ok_btn)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.addKennelFragment, true)
            .build()

        okBtn?.setOnClickListener {
            dialog.dismiss()
            kennelPropertiesRepository.saveKennelAvatarUri("")
            findNavController()
                .navigate(
                    R.id.action_kennelConfirmFragment_to_addKennelFragment,
                    null,
                    navOptions
                )
        }
    }


}