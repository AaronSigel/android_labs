package com.example.labs_app.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.util.Log

/**
 * Базовая Activity с логированием жизненного цикла в Logcat.
 * Позволяет однозначно идентифицировать Activity и вызванный lifecycle-метод.
 */
abstract class BaseLoggedActivity : ComponentActivity() {

    /** Тег для Logcat: имя конкретной Activity. */
    protected val lifecycleTag: String
        get() = "LabsApp/${this::class.simpleName}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(lifecycleTag, "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d(lifecycleTag, "onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(lifecycleTag, "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(lifecycleTag, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(lifecycleTag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(lifecycleTag, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(lifecycleTag, "onDestroy")
    }
}
