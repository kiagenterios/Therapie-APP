package com.psych.therapieapp.data.local

import androidx.room.TypeConverter
import com.psych.therapieapp.data.local.entity.ExerciseCategory
import com.psych.therapieapp.data.local.entity.FeatureType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Zentrale Room-TypeConverters fuer die Datenbank.
 */
class Converters {

    // ----- ExerciseCategory -----
    @TypeConverter
    fun fromExerciseCategory(value: ExerciseCategory): String = value.name

    @TypeConverter
    fun toExerciseCategory(value: String): ExerciseCategory =
        runCatching { ExerciseCategory.valueOf(value) }
            .getOrDefault(ExerciseCategory.MINDFULNESS)

    // ----- FeatureType -----
    @TypeConverter
    fun fromFeatureType(value: FeatureType): String = value.name

    @TypeConverter
    fun toFeatureType(value: String): FeatureType =
        runCatching { FeatureType.valueOf(value) }
            .getOrDefault(FeatureType.MOOD)

    // ----- LocalDate -----
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    // ----- LocalDateTime -----
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    // ----- LocalTime -----
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    // ----- Instant -----
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    // ----- Set<Int> als CSV-String -----
    @TypeConverter
    fun fromIntSet(value: Set<Int>): String =
        value.sorted().joinToString(",")

    @TypeConverter
    fun toIntSet(value: String): Set<Int> =
        if (value.isBlank()) emptySet()
        else value.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()

    // ----- List<String> als JSON-aehnlicher String -----
    @TypeConverter
    fun fromStepList(steps: List<String>): String {
        val sb = StringBuilder()
        sb.append('[')
        steps.forEachIndexed { idx, s ->
            if (idx > 0) sb.append(',')
            sb.append('"')
            sb.append(escape(s))
            sb.append('"')
        }
        sb.append(']')
        return sb.toString()
    }

    @TypeConverter
    fun toStepList(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        val trimmed = json.trim().removePrefix("[").removeSuffix("]")
        if (trimmed.isBlank()) return emptyList()

        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inString = false
        var escapeFlag = false
        for (c in trimmed) {
            when {
                escapeFlag -> {
                    current.append(c)
                    escapeFlag = false
                }
                c == '\\' && inString -> escapeFlag = true
                c == '"' -> {
                    if (inString) {
                        result.add(current.toString())
                        current.clear()
                    }
                    inString = !inString
                }
                inString -> current.append(c)
                else -> { /* Komma/Whitespace zwischen Strings */ }
            }
        }
        return result
    }

    private fun escape(s: String): String =
        s.replace("\\", "\\\\").replace("\"", "\\\"")
}
