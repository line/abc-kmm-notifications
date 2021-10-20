package androidApp.sample

import PayloadBody
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.linecorp.abc.notifications.ABCDeviceToken
import com.linecorp.abc.notifications.ABCNotifications
import com.linecorp.abc.notifications.extension.onDeletedMessages

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ABCNotifications
            .onNewToken(this) {
                println("onNewToken -> ${ABCDeviceToken.FCMToken}")
            }
            .onDeletedMessages(this) {
                println("onDeletedMessages")
            }
            .onMessageReceived(this) {
                // FCM RemoteMessage
                val remoteMessage = it.remoteMessage

                // decode to Payload with PayloadBody
                val payload = it.decodedPayload<PayloadBody>()

                // decode to PayloadBody
                val data = it.decodedData<PayloadBody>()
            }
            .beginListening()
    }
}