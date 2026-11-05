package com.example.habittracker.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        val dateString = json.asString ?: throw JsonParseException("Missing date value")
        return try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            try {
                val trimmed = if (dateString.endsWith("Z")) dateString.dropLast(1) else dateString
                LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_DATE_TIME)
            } catch (_: Exception) {
                OffsetDateTime.parse(dateString).atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
            }
        }
    }

    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return src?.let {
            JsonPrimitive(it.format(DateTimeFormatter.ISO_DATE_TIME))
        } ?: JsonNull.INSTANCE
    }
}
