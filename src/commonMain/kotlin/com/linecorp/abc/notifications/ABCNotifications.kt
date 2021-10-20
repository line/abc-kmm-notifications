package com.linecorp.abc.notifications

import com.linecorp.abc.notifications.model.NotificationElement
import kotlin.native.concurrent.ThreadLocal

typealias OnMessageReceivedBlock = (NotificationElement) -> Unit
typealias UnitBlock = () -> Unit

class ABCNotifications {

    @ThreadLocal
    companion object {

        // -------------------------------------------------------------------------------------------
        //  Public
        // -------------------------------------------------------------------------------------------

        val numberOfNotifications: Int
            get() = elements.count()

        var isUseFCMOnIOS: Boolean
            get() = manager.isUseFCMOnIOS
            set(value) { manager.isUseFCMOnIOS = value }

        fun beginListening() {
            isInitialized = true
            dequeue()
        }

        fun onMessageReceived(target: Any, block: OnMessageReceivedBlock): Companion {
            onMessageReceivedMap[target] = block
            return this
        }

        fun onNewToken(target: Any, block: UnitBlock): Companion {
            onNewTokenMap[target] = block
            return this
        }

        fun removeAllListeners() {
            onMessageReceivedMap.clear()
            onNewTokenMap.clear()
            manager.removeAllListeners()
        }

        fun removeListeners(target: Any) {
            onMessageReceivedMap.remove(target)
            onNewTokenMap.remove(target)
            manager.removeListeners(target)
        }

        fun unregister() {
            isInitialized = false
            clearNotifications()
            manager.unregister()
        }

        // -------------------------------------------------------------------------------------------
        //  Internal
        // -------------------------------------------------------------------------------------------

        internal val manager: NotificationManager by lazy {
            NotificationManager()
        }

        internal fun clearNotifications() {
            elements.clear()
        }

        internal fun notifyOnNewToken() {
            onNewTokenMap.forEach {
                it.value.invoke()
            }
        }

        internal fun processReceived(source: Any, isInactive: Boolean) {
            val element = NotificationElement(isInactive, source)
            enqueue(element).dequeue()
        }

        // -------------------------------------------------------------------------------------------
        //  Private
        // -------------------------------------------------------------------------------------------

        private var isInitialized = false
        private var elements = mutableListOf<NotificationElement>()
        private val onMessageReceivedMap = mutableMapOf<Any, OnMessageReceivedBlock>()
        private val onNewTokenMap = mutableMapOf<Any, UnitBlock>()

        private fun dequeue() {
            if (!isInitialized ||
                !ABCDeviceToken.isValid ||
                elements.count() < 1) {
                return
            }
            val element = elements.removeFirst()
            notifyOnMessageReceived(element)
            dequeue()
        }

        private fun enqueue(element: NotificationElement): Companion {
            elements.add(element)
            return this
        }

        private fun notifyOnMessageReceived(element: NotificationElement) {
            onMessageReceivedMap.forEach {
                it.value.invoke(element)
            }
        }
    }
}