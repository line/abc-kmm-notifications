package com.linecorp.abc.notifications

import cocoapods.FirebaseMessaging.FIRMessaging
import com.linecorp.abc.notifications.extention.toByteArray
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.stringWithFormat
import platform.UIKit.*
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationResponse

class ABCNotificationCenterDelegate {

    @ThreadLocal
    companion object {

        fun applicationDidFinishLaunching(options: Map<UIApplicationLaunchOptionsKey, Any>?) {
            val remote = options?.get(UIApplicationLaunchOptionsRemoteNotificationKey)
            val local = options?.get(UIApplicationLaunchOptionsLocalNotificationKey)
            val userInfo = remote ?: local ?: return
            ABCNotifications.processReceived(userInfo, true)
        }

        fun applicationDidReceive(notification: UILocalNotification) {
            val userInfo = notification.userInfo ?: return
            val isInactive = UIApplication.sharedApplication.applicationState == UIApplicationState.UIApplicationStateInactive
            ABCNotifications.processReceived(userInfo, isInactive)
        }

        fun applicationDidReceiveRemoteNotification(userInfo: Map<*, Any>) {
            val isInactive = UIApplication.sharedApplication.applicationState == UIApplicationState.UIApplicationStateInactive
            ABCNotifications.processReceived(userInfo, isInactive)
        }

        fun applicationDidRegisterNotification() {
            UIApplication.sharedApplication.registerForRemoteNotifications()
        }

        fun applicationDidRegisterForRemoteNotifications(deviceToken: NSData) {
            if (deviceToken.length < 1u) { return }
            val tokenString = deviceToken
                .toByteArray()
                .joinToString("") {
                    NSString.stringWithFormat("%02.2hhX", it)
                }
            if (tokenString == ABCDeviceToken.rawToken) { return }
            if (ABCNotifications.isUseFCMOnIOS) {
                FIRMessaging.messaging().autoInitEnabled = true
                FIRMessaging.messaging().APNSToken = deviceToken
            }
            ABCDeviceToken.rawToken = tokenString
            ABCNotifications.notifyOnNewToken()
        }

        fun userNotificationCenterWillPresent(notification: UNNotification) {
            ABCNotifications.processReceived(notification.request.content.userInfo, false)
        }

        fun userNotificationCenterDidReceive(response: UNNotificationResponse) {
            ABCNotifications.processReceived(response.notification.request.content.userInfo, true)
        }
    }
}