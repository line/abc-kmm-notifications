package com.linecorp.abc.notifications.extension

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

internal fun List<*>.toJsonElement(): JsonElement {
    val list: MutableList<JsonElement> = mutableListOf()
    this.forEach {
        val value = it ?: return@forEach
        when(value) {
            is Map<*, *> -> list.add((value).toJsonElement())
            is List<*> -> list.add(value.toJsonElement())
            is String -> list.add(if (value.isNotEmpty()) {
                Json.parseToJsonElement(value)
            } else {
                JsonPrimitive(value)
            })
            else -> list.add(JsonPrimitive(value.toString()))
        }
    }
    return JsonArray(list)
}