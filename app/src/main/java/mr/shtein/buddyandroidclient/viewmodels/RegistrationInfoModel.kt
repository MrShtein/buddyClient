package mr.shtein.buddyandroidclient.viewmodels

import androidx.lifecycle.ViewModel

class RegistrationInfoModel(
    var email: String = "",
    var password: String = "",
): ViewModel() {

    fun checkKennelInfo(): Boolean {
        if (email.isEmpty() || password.isEmpty()) return false
        return true
    }




}