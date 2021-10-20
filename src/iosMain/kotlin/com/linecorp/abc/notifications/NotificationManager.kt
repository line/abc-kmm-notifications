package com.linecorp.abc.notifications

import cocoapods.FirebaseMessaging.*
import platform.UIKit.*
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.freeze

internal actual class NotificationManager : NSObject(), FIRMessagingDelegateProtocol {

    actual var isUseFCMOnIOS: Boolean
        get() = FIRMessaging.messaging().delegate != null
        set(value) {
            FIRMessaging.messaging().delegate = if (value) this else null
        }

    actual fun removeAllListeners() { }

    actual fun removeListeners(target: Any) { }

    actual fun unregister() {
        UIApplication.sharedApplication.unregisterForRemoteNotifications()
    }

    override fun messaging(messaging: FIRMessaging, didReceiveRegistrationToken: String?) {
        val token = didReceiveRegistrationToken ?: return
        if (token.isEmpty() || token == ABCDeviceToken.FCMToken) { return }
        ABCDeviceToken.FCMToken = token
        ABCNotifications.notifyOnNewToken()
    }

    fun register(settings: NotificationSettings) {
        val types = settings.types.toULong()
        if (UIDevice.currentDevice.systemVersion > "10") {
            val center = UNUserNotificationCenter.currentNotificationCenter()
            center.delegate = UIApplication.sharedApplication.delegate as? UNUserNotificationCenterDelegateProtocol
            center.requestAuthorizationWithOptions(types, withFreezing { _, error ->
                if (error == null) {
                    dispatch_async(dispatch_get_main_queue(), withFreezing {
                        UIApplication.sharedApplication.registerForRemoteNotifications()
                    })
                }
            })
        } else {
            UIApplication.sharedApplication.registerUserNotificationSettings(
                UIUserNotificationSettings.settingsForTypes(types, null)
            )
        }
    }

    private fun <T> withFreezing(block: T): T {
        block.freeze()
        return block
    }
}
