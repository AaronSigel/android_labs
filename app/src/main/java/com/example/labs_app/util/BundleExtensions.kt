package com.example.labs_app.util

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

/**
 * Совместимый getParcelable для minSdk < 33 (двухаргументный доступен с API 33).
 */
inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key) as? T
    }
}
