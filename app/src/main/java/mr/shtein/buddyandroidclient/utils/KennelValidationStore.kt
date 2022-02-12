package mr.shtein.buddyandroidclient.utils

data class KennelValidationStore(
    var isValidName: Boolean = false,
    var isValidPhone: Boolean = false,
    var isValidEmail: Boolean = false,
    var isValidCity: Boolean = false,
    var isValidStreet: Boolean = false,
    var isValidHouseNum: Boolean = false,
    var isValidBuilding: Boolean = false,
    var isValidIdentificationNum: Boolean = false
)
