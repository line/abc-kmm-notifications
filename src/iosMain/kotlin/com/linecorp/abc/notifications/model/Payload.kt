package com.linecorp.abc.notifications.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
actual data class Payload<T>(
    val experienceId: String = "",
    val aps: APS = APS(),
    val body: T?,
) {
    @Serializable
    data class APS(
        val badge: String = "",
        val category: String = "",
        val sound: String = "",

        @SerialName("thread-id")
        val threadId: String = "",

        val alert: Alert = Alert(),
    )

    @Serializable
    data class Alert(
        val body: String = "",
        val subtitle: String = "",
        val title: String = "",

        @SerialName("launch-image")
        val launchImage: String = "",
    )
}