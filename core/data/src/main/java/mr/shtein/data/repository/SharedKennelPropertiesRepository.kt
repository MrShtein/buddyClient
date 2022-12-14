package mr.shtein.data.repository

import mr.shtein.data.util.SharedPreferences

const val KENNEL_AVATAR_URI_KEY = "kennel_avatar_uri"

class SharedKennelPropertiesRepository(private val storage: SharedPreferences) : KennelPropertiesRepository {

    override fun getKennelAvatarUri(): String {
        return storage.readString(KENNEL_AVATAR_URI_KEY, "")
    }

    override fun saveKennelAvatarUri(avatarUri: String) {
        storage.writeString(KENNEL_AVATAR_URI_KEY, avatarUri)
    }

    override fun removeAll() {
        storage.cleanAllData()
    }
}