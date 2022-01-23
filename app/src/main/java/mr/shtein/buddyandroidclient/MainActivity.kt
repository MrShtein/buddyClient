package mr.shtein.buddyandroidclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    val test: String = "ok"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        }

    companion object {
        const val IS_FROM_REGISTRATION_KEY = "is_from_registration"
        const val PERSISTENT_STORAGE_NAME: String = "buddy_storage"
        const val TOKEN_KEY = "token_key"
        const val USER_ID_KEY = "id"
        const val USER_LOGIN_KEY = "user_login"
        const val USER_ROLE_KEY = "user_role"
        const val IS_LOCKED_KEY = "is_locked"
        const val USER_NAME_KEY = "user_name"
        const val USER_SURNAME_KEY = "user_surname"
        const val USER_PHONE_NUMBER_KEY = "user_phone_number"
    }
}

