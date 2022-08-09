package mr.shtein.buddyandroidclient.data.repository

interface KennelPropertiesRepository {
    fun getKennelAvatarUri(): String
    fun saveKennelAvatarUri(avatarUri: String)

    fun removeAll()
}