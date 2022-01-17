package mr.shtein.buddyandroidclient.viewmodels

import androidx.lifecycle.ViewModel

class RegistrationInfoModel(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var isCheckedName: Boolean = false,
    var isCheckedEmail: Boolean = false,
    var isCheckedPassword: Boolean = false,
    var isCheckedRepeatPassword: Boolean = false
) : ViewModel()