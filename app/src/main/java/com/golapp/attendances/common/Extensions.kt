package com.golapp.attendances.common

import com.golapp.attendances.BuildConfig

fun String.toAbsoluteUrl(): String {
    val raw = trim()
    if (raw.isBlank()) return raw
    if (raw.startsWith("http://") || raw.startsWith("https://")) return raw
    return BuildConfig.API_URL.trimEnd('/') + "/" + raw.trimStart('/')
}

fun String.scheduleInline(): String =
    split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString("  |  ")