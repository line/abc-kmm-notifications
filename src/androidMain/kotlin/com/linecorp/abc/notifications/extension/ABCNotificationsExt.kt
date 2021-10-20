package com.linecorp.abc.notifications.extension

import com.linecorp.abc.notifications.ABCNotifications
import com.linecorp.abc.notifications.UnitBlock

fun ABCNotifications.Companion.onDeletedMessages(target: Any, block: UnitBlock): ABCNotifications.Companion {
    manager.setOnDeletedMessages(target, block)
    return this
}