package mr.shtein.buddyandroidclient

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.kakao.text.KButton
import mr.shtein.buddyandroidclient.screen.BottomNavigationScreen
import mr.shtein.buddyandroidclient.screen.LoginScreen

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest : TestCase() {

    @Rule
    @JvmField
    val rule = ActivityTestRule(MainActivity::class.java, false, true)


    @Test
    fun checkTextInLoginScreen() {
        before {

        }.after {

        }.run {
            step("Open bottomDialog with login and registration button") {
                BottomNavigationScreen {
                    kennelBtn {
                        flakySafely(
                            timeoutMs = 10000,
                            intervalMs = 4000,
                            null,
                            null
                        ) {
                            click()
                        }
                    }
                }
            }
            step("Push to login button") {
                val loginBtn = KButton {
                    withId(R.id.to_login_fragment_button)
                }
                loginBtn.click()
            }
            step("Check login fragment UI") {
                LoginScreen {
                    email {
                        withHint(R.string.e_mail)
                    }
                    password {
                        withHint(R.string.password)
                    }
                    nextBtn {
                        hasText(R.string.next)
                    }
                    resetPwdBtn {
                        hasText(R.string.forget_password_question)
                    }

                }
            }
        }

    }
}