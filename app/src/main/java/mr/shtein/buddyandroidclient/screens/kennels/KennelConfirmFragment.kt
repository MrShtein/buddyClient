package mr.shtein.buddyandroidclient.screens.kennels

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import mr.shtein.buddyandroidclient.R
import mr.shtein.buddyandroidclient.model.KennelRequest
import java.lang.StringBuilder

class KennelConfirmFragment: Fragment(R.layout.kennel_confirm_fragment) {

    companion object {
        private const val SETTINGS_DATA_KEY = "settings_data"
    }

    private lateinit var settingsData: KennelRequest
    private lateinit var avatarImg: ShapeableImageView
    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var email: TextView
    private lateinit var cityAndRegion: TextView
    private lateinit var street: TextView
    private lateinit var identificationNum: TextView
    private lateinit var saveBtn: MaterialButton



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments ?: bundleOf()
        settingsData = Gson()
            .fromJson(bundle.getString(SETTINGS_DATA_KEY), KennelRequest::class.java)

        initViews(view)
        setViews()

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
            strBuilder.append("корп. ${settingsData.kennelBuildingNum}")
        }
        return strBuilder.toString()
    }


}