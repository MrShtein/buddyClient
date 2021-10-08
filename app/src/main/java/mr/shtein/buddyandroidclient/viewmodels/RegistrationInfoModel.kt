package mr.shtein.buddyandroidclient.viewmodels

import androidx.lifecycle.ViewModel

class RegistrationInfoModel(
    var kennelName: String = "",
    var city: String = "",
    var street: String = "",
    var house: String = "",
    var phoneNumber: String = ""
): ViewModel() {

    fun checkKennelInfo(): Boolean {
        if (kennelName.isEmpty() || city.isEmpty() || street.isEmpty() || house.isEmpty()
            || phoneNumber.isEmpty()) return false
        return true
    }

}