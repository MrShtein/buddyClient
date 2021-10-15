package mr.shtein.buddyandroidclient.viewmodels

import androidx.lifecycle.ViewModel

class RegistrationInfoModel(
    var email: String = "",
    var password: String = "",
    var isCheckedEmail: Boolean = false,
    var isCheckedPassword: Boolean = false,
    var isCheckedRepeatPassword: Boolean = false
) : ViewModel()