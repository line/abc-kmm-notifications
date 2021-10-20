import androidx.core.os.bundleOf
import com.google.firebase.messaging.RemoteMessage
import com.linecorp.abc.notifications.model.NotificationElement
import kotlinx.serialization.Serializable
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@Serializable
data class Body(
    val notificationType: String = "",
    val orderId: Int = 0,
)

class NotificationElementTest {

    @Before
    fun setUp() {
    }

    @Test
    fun testDecodedPayload() {
        val body = mapOf(
            "notificationType" to "type1",
            "orderId" to 1,
        )
        val bundle = bundleOf(
            "projectId" to "0e2cd410-d9f3-11e9-8cdf-2d5354cfe118",
            "experienceId" to "DemaecanDriver_STG",
            "scopeKey" to "DemaecanDriver_STG",
            "body" to JSONObject(body).toString(),
            "title" to "hello",
            "message" to "world",
        )

        val remoteMessage = RemoteMessage(bundle)
        val element = NotificationElement(false, remoteMessage)
        val payload = element.decodedPayload<Body>()

        assertEquals("0e2cd410-d9f3-11e9-8cdf-2d5354cfe118", payload.projectId)
        assertEquals("DemaecanDriver_STG", payload.experienceId)
        assertEquals("DemaecanDriver_STG", payload.scopeKey)
        assertEquals("hello", payload.title)
        assertEquals("world", payload.message)
        assertEquals("type1", payload.body?.notificationType)
        assertEquals(1, payload.body?.orderId)
    }

    @Test
    fun testDecodedData() {
        val data = mapOf(
            "notificationType" to "type1",
            "orderId" to 1,
        )
        val bundle = bundleOf(
            "title" to "title",
            "body" to "body",
            "data" to JSONObject(data).toString()
        )
        val remoteMessage = RemoteMessage(bundle)
        val element = NotificationElement(false, remoteMessage)
        val decodedData = element.decodedData<Body>()

        assertEquals("title", element.remoteMessage.notification?.title)
        assertEquals("body", element.remoteMessage.notification?.body)
        assertEquals("type1", decodedData.notificationType)
        assertEquals(1, decodedData.orderId)
    }
}