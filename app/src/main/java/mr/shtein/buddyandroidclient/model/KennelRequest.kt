package mr.shtein.buddyandroidclient.model

data class KennelRequest(
    val kennelAvtUrl: String,
    val kennelName: String,
    val kennelPhoneNum: String,
    val kennelEmail: String,
    val kennelCity: Long,
    val kennelStreet: String,
    val kennelHouseNum: Int,
    val kennelBuildingNum: String,
    val kennelIdentifyNum: Int
)
