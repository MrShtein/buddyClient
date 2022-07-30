package mr.shtein.buddyandroidclient.screen

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.bottomnav.KBottomNavigationView
import io.github.kakaocup.kakao.common.views.KView
import mr.shtein.buddyandroidclient.LoginFragment
import mr.shtein.buddyandroidclient.MainActivity
import mr.shtein.buddyandroidclient.R

object BottomNavigationScreen : KScreen<BottomNavigationScreen>() {
    override val layoutId: Int = R.layout.activity_main
    override val viewClass: Class<*> = MainActivity::class.java

    val bottomNavigation = KBottomNavigationView {
        withId(R.id.bottom_nav_view)
    }

    val kennelBtn = KView {
        withId(R.id.kennel_graph)
    }

    val userProfileBtn = KView {
        withId(R.id.profile_graph)
    }
}