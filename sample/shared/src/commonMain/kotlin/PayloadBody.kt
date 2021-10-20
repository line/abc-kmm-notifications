import com.linecorp.abc.notifications.ABCNotifications
import kotlinx.serialization.Serializable

@Serializable
data class PayloadBody(
    val notificationType: String = "",
    val orderId: String = "",
)

fun ABCNotifications.Companion.configure(block: ABCNotifications.Companion.() -> Unit) {
    apply(block)

    onNewToken(this) {
        // TODO: send to register ${ABCDeviceToken.value} to server
    }.beginListening()
}