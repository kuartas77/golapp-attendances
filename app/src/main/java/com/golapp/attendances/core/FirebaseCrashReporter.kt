package com.golapp.attendances.core

import com.golapp.attendances.domain.CrashReporter
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import javax.inject.Inject

class FirebaseCrashReporter @Inject constructor() : CrashReporter {
    override fun setCustomKey(key: String, value: String) {
        Firebase.crashlytics.setCustomKey(key, value)
    }

    override fun log(message: String) {
        Firebase.crashlytics.log(message)
    }

    override fun recordException(t: Throwable) {
        Firebase.crashlytics.recordException(t)
    }
}