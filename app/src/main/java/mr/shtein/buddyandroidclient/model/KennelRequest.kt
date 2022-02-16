package mr.shtein.buddyandroidclient.model

data class KennelRequest(
    var kennelAvtUri: String = "",
    var kennelName: String = "",
    var kennelPhoneNum: String = "",
    var kennelEmail: String = "",
    var kennelCity: String = "",
    var kennelStreet: String = "",
    var kennelHouseNum: String = "",
    var kennelBuildingNum: String = "",
    var kennelIdentifyNum: Long = 0
)
