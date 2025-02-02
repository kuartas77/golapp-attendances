package com.golapp.attendances.common

import kotlin.coroutines.cancellation.CancellationException

inline fun <T, R> T.resultOf(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
