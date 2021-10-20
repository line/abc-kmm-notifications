package com.linecorp.abc.notifications

enum class UserNotificationType(val value: UInt) {
    Badge(1u.shl(0)),
    Sound(1u.shl(1)),
    Alert(1u.shl(2)),
}