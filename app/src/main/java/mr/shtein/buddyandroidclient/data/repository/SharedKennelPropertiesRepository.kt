package mr.shtein.buddyandroidclient.data.repository

import android.security.keystore.KeyNotYetValidException
import mr.shtein.buddyandroidclient.utils.SharedPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier

const val KENNEL_STORE_NAME = "kennelStore"
const val KENNEL_AVATAR_URI_KEY = "kennel_avatar_uri"


class SharedKennelPropertiesRepository() : KennelPropertiesRepository, KoinComponent {

    private val storage: SharedPreferences by inject(qualifier = named(KENNEL_STORE_NAME))

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