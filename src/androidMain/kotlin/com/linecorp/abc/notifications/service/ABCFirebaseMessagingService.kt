package com.linecorp.abc.notifications.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.linecorp.abc.notifications.ABCDeviceToken
import com.linecorp.abc.notifications.ABCNotifications

open class ABCFirebaseMessagingService: FirebaseMessagingService() {

    override fun onDeletedMessages() {
        ABCNotifications.manager.notifyOnDeletedMessages()
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        ABCNotifications.processReceived(p0, false)
    }

    override fun onNewToken(p0: String) {
        ABCNotifications.manager.updateToke(p0)
    }
}