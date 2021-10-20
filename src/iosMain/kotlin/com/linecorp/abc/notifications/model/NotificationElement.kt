package com.linecorp.abc.notifications.model

import com.linecorp.abc.notifications.extension.decoded

actual class NotificationElement actual constructor(
    isInactive: Boolean,
    source: Any,
) {
    val isInactive = isInactive
    val userInfo = source as Map<*, *>

    @Throws(Throwable::class)
    actual inline fun <reified T> decodedPayload(): Payload<T> = userInfo.decoded()
}