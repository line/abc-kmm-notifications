package com.linecorp.abc.notifications.extention

import com.linecorp.abc.notifications.ABCNotifications
import com.linecorp.abc.notifications.NotificationSettings

fun ABCNotifications.Companion.registerSettings(block: NotificationSettings.() -> Unit): ABCNotifications.Companion {
    val settings = NotificationSettings().apply(block)
    manager.register(settings)
    return this
}