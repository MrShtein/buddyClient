package mr.shtein.data.repository

interface KennelPropertiesRepository {
    fun getKennelAvatarUri(): String
    fun saveKennelAvatarUri(avatarUri: String)

    fun removeAll()
}