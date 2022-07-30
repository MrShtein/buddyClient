package mr.shtein.buddyandroidclient.screen

import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.screen.Screen
import mr.shtein.buddyandroidclient.LoginFragment
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.text.KButton
import mr.shtein.buddyandroidclient.R

object LoginScreen : KScreen<LoginScreen>() {

    override val layoutId: Int = R.layout.fragment_login
    override val viewClass: Class<*> = LoginFragment::class.java

    val email = KEditText {
        withId(R.id.login_main_text)
    }

    val password = KEditText {
        withId(R.id.login_password_input_container)
    }

    val nextBtn = KButton {
        withId(R.id.login_button)
    }

    val resetPwdBtn = KButton {
        withId(R.id.login_fragment_forgot_password_btn)
    }
}