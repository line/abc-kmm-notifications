import com.linecorp.abc.notifications.ABCNotificationCenterDelegate
import com.linecorp.abc.notifications.ABCNotifications
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationLaunchOptionsRemoteNotificationKey
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ABCNotificationCenterDelegateTest {

    @BeforeTest
    fun setUp() {
    }

    @Test
    fun testApplicationDidFinishLaunchingWithOptions() {
        val payload = mapOf(
            "aps" to mapOf(
                "sound" to "",
                "alert" to mapOf(
                    "body" to "body",
                    "title" to "title"
                )
            )
        )
        val options = mapOf(
            UIApplicationLaunchOptionsRemoteNotificationKey to payload
        )

        ABCNotificationCenterDelegate.applicationDidFinishLaunching(options)

        assertEquals(1, ABCNotifications.numberOfNotifications)
    }
}