package com.linecorp.abc.notifications.model

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class NotificationElement(
    isInactive: Boolean,
    source: Any,
) {
    inline fun <reified T> decodedPayload(): Payload<T>
}