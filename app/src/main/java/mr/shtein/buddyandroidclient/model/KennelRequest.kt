package mr.shtein.buddyandroidclient.model

data class KennelRequest(
    var userId: Long = 0,
    var kennelAvtUri: String = "",
    var kennelName: String = "",
    var kennelPhoneNum: String = "",
    var kennelEmail: String = "",
    var kennelCity: String = "",
    var kennelStreet: String = "",
    var kennelHouseNum: String = "",
    var kennelBuildingNum: String = "",
    var kennelIdentifyNum: Long = 0,
    var role: String
)
