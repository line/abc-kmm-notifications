package com.linecorp.abc.notifications

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect class NotificationManager() {
    var isUseFCMOnIOS: Boolean
    fun removeAllListeners()
    fun removeListeners(target: Any)
    fun unregister()
}