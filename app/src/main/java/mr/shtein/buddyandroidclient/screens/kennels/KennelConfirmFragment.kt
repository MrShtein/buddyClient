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
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import mr.shtein.buddyandroidclient.model.AvatarWrapper
import mr.shtein.buddyandroidclient.model.KennelRequest
import mr.shtein.buddyandroidclient.retrofit.Common
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    private lateinit var storage: SharedPreferences
    private var layout: MotionLayout? = null
    private var coroutineScope = CoroutineScope(Dispatchers.Main + Job())


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
        storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
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
            .append(" ??.")
            .append(settingsData.kennelHouseNum)
        if (settingsData.kennelBuildingNum != "") {
            strBuilder.append(" ????????. ${settingsData.kennelBuildingNum}")
        }
        return strBuilder.toString()
    }

    private fun setListeners() {
        saveBtn.setOnClickListener {
            saveBtn.isClickable = false
            progressBar.isVisible = true

            coroutineScope.launch {
                try {
                    val avatarWrapper: AvatarWrapper? = getPhotoAndType()
                    val response = addNewKennel(avatarWrapper)
                    if (response.isSuccessful) {
                        storage.writeString(SharedPreferences.USER_ROLE_KEY, ADMIN_ROLE_TXT)
                        avatarWrapper?.file?.delete()
                        showDialog()

                    } else {
                        when (response.code()) {
                            403 -> TODO()
                            404 -> TODO()
                        }
                    }

                } catch (ex: FileNotFoundException) {
                    Log.e(
                        "error",
                        "???? ???????????????????? ?????????? ????????"
                    ) //TODO ?????????? ???????????? exceptions, ?????????????? ?????????? ??????????????????
                } catch (ex: SocketTimeoutException) {
                    Log.e(
                        "error",
                        "??????-???? ?????????? ???? ?????? ?? ???????????? ???? ??????????????"
                    )
                }

            }
        }
    }

    private suspend fun addNewKennel(avatarWrapper: AvatarWrapper?): Response<Boolean> {
        val retrofit = Common.retrofitService
        var requestFile: RequestBody? = null
        var body: MultipartBody.Part? = null


        val storage = SharedPreferences(requireContext(), SharedPreferences.PERSISTENT_STORAGE_NAME)
        val token = storage.readString(SharedPreferences.TOKEN_KEY, "")
        val headers = mutableMapOf<String, String>()
        headers["Authorization"] = token

        settingsData.userId = storage.readLong(SharedPreferences.USER_ID_KEY, 0)
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
            retrofit.addNewKennel(headers, requestBody, body)
        } else {
            retrofit.addNewKennel(headers, requestBody, null)
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

    private fun showDialog() {

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyDialog)

            .setView(R.layout.kennel_confirm_dialog)
            .setBackground(ColorDrawable(requireContext().getColor(R.color.transparent)))
            .show()


        val okBtn: Button? = dialog.findViewById(R.id.user_settings_dialog_ok_btn)
        layout = dialog.findViewById(R.id.kennel_confirm_dialog_layout)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.addKennelFragment, true)
            .build()

        okBtn?.setOnClickListener {
            dialog.dismiss()
            storage.writeString(SharedPreferences.KENNEL_AVATAR_URI_KEY, "")
            findNavController()
                .navigate(
                    R.id.action_kennelConfirmFragment_to_addKennelFragment,
                    null,
                    navOptions
                )
        }
    }


}