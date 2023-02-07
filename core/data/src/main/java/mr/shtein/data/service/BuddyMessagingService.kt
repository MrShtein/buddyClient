package mr.shtein.data.service

import com.google.firebase.messaging.FirebaseMessagingService

class BuddyMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
