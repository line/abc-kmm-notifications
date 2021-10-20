package com.linecorp.abc.notifications.model

import kotlinx.serialization.Serializable

@Serializable
actual data class Payload<T>(
    val projectId: String = "",
    val experienceId: String = "",
    val scopeKey: String = "",
    val title: String = "",
    val message: String = "",
    val channelId: String = "",
    val categoryId: String = "",
    val icon: String = "",
    val link: String = "",
    val sound: String = "",
    val vibrate: String = "",
    val priority: String = "",
    val badge: String = "",
    val body: T?,
)