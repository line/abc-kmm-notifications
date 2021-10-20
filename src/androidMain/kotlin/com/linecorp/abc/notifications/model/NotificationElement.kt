package com.linecorp.abc.notifications.model

import com.google.firebase.messaging.RemoteMessage
import com.linecorp.abc.notifications.extension.decoded

actual class NotificationElement actual constructor(
    isInactive: Boolean,
    source: Any,
) {
    val remoteMessage = source as RemoteMessage

    actual inline fun <reified T> decodedPayload(): Payload<T> = remoteMessage.data.decoded()

    inline fun <reified T> decodedData(): T = remoteMessage.data.decoded()
}