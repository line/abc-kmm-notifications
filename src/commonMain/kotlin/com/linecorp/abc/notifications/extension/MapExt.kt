package com.linecorp.abc.notifications.extension

import kotlinx.serialization.json.*

@Throws(Throwable::class)
inline fun <reified T> Map<*, *>.decoded(): T = Json {
    coerceInputValues = true
    encodeDefaults = true
    ignoreUnknownKeys = true
    isLenient = true
}.decodeFromJsonElement(toJsonElement())

fun Map<*, *>.toJsonElement(): JsonElement {
    val map: MutableMap<String, JsonElement> = mutableMapOf()
    this.forEach {
        val key = it.key as? String ?: return@forEach
        val value = it.value ?: return@forEach
        when(value) {
            is Map<*, *> -> map[key] = (value).toJsonElement()
            is List<*> -> map[key] = value.toJsonElement()
            is String -> map[key] = if (value.isNotEmpty()) {
                try {
                    Json.parseToJsonElement(value)
                } catch (e: Exception) {
                    JsonPrimitive(value)
                }
            } else {
                JsonPrimitive(value)
            }
            else -> map[key] = JsonPrimitive(value.toString())
        }
    }
    return JsonObject(map)
}