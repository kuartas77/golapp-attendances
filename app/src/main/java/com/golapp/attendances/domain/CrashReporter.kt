package com.golapp.attendances.domain

interface CrashReporter {
    fun setCustomKey(key: String, value: String)
    fun log(message: String)
    fun recordException(t: Throwable)
}