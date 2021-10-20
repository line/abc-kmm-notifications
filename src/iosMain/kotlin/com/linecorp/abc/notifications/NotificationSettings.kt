package com.linecorp.abc.notifications

class NotificationSettings {

    var types = 0u
        private set

    fun add(type: UserNotificationType) {
        if (hasNotType(type)) {
            types = types.or(type.value)
        }
    }

    @Suppress("UNUSED")
    fun hasNotType(type: UserNotificationType) =
        !hasType(type)

    fun hasType(type: UserNotificationType) =
        types.and(type.value) == type.value
}