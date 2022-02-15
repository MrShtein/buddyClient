package mr.shtein.buddyandroidclient.utils

data class KennelValidationStore(
    val mapOfValues: MutableMap<String, Boolean> = mutableMapOf()
) {
    init {
        mapOfValues["isValidName"] = false
        mapOfValues["isValidPhone"] = false
        mapOfValues["isValidEmail"] = false
        mapOfValues["isValidCity"] = false
        mapOfValues["isValidStreet"] = false
        mapOfValues["isValidHouseNum"] = false
        mapOfValues["isValidIdentificationNum"] = false
    }
}
