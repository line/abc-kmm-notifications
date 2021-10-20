package com.linecorp.abc.notifications

import com.google.firebase.messaging.FirebaseMessaging

internal actual class NotificationManager {

    private val onDeletedMessagesMap = mutableMapOf<Any, UnitBlock>()

    init {
        if (ABCDeviceToken.FCMToken.isEmpty() && ABCDeviceToken.rawToken.isNotEmpty()) {
            ABCDeviceToken.FCMToken = ABCDeviceToken.rawToken
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateToke(task.result.toString())
            }
        }
    }

    actual var isUseFCMOnIOS = false

    actual fun removeAllListeners() {
        onDeletedMessagesMap.clear()
    }

    actual fun removeListeners(target: Any) {
        onDeletedMessagesMap.remove(target)
    }

    actual fun unregister() { }

    fun notifyOnDeletedMessages() {
        onDeletedMessagesMap.forEach {
            it.value.invoke()
        }
    }

    fun setOnDeletedMessages(target: Any, block: UnitBlock) {
        onDeletedMessagesMap[target] = block
    }

    internal fun updateToke(token: String) {
        if (token.isEmpty() || token == ABCDeviceToken.rawToken) { return }
        ABCDeviceToken.rawToken = token
        ABCDeviceToken.FCMToken = token
        ABCNotifications.notifyOnNewToken()
    }
}