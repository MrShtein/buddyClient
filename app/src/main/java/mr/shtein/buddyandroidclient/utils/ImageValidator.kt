package mr.shtein.buddyandroidclient.utils

import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mr.shtein.buddyandroidclient.exceptions.validate.NoPhotoException
import mr.shtein.buddyandroidclient.exceptions.validate.TooShortLengthException
import mr.shtein.buddyandroidclient.model.ImageContainer

class ImageValidator() {

    fun hasPhotoChecker(valuesForValidate: MutableList<String>): Boolean {
        return valuesForValidate.size == 0
    }


}