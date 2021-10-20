package com.linecorp.abc.notifications

import com.linecorp.abc.sharedstorage.SharedStorage
import kotlin.native.concurrent.ThreadLocal

class ABCDeviceToken {

    @ThreadLocal
    companion object {
        private const val keyForFCMToken = "ABCDeviceToken::FCMToken"
        private const val keyForValue = "ABCDeviceToken::value"

        val isValid: Boolean
            get() = if (ABCNotifications.manager.isUseFCMOnIOS) {
                rawToken.isNotEmpty() && FCMToken.isNotEmpty()
            } else {
                rawToken.isNotEmpty()
            }

        var FCMToken: String
            get() = SharedStorage.load(keyForFCMToken, "")
            internal set(value) = SharedStorage.save(value, keyForFCMToken)

        var rawToken: String
            get() = SharedStorage.load(keyForValue, "")
            internal set(value) = SharedStorage.save(value, keyForValue)

        fun clear() {
            SharedStorage.save("", keyForFCMToken)
            SharedStorage.save("", keyForValue)
        }
    }
}