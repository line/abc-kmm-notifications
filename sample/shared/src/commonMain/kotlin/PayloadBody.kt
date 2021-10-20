import kotlinx.serialization.Serializable

@Serializable
data class PayloadBody(
    val notificationType: String = "",
    val orderId: String = "",
)