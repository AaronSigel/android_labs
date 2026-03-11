package com.example.labs_app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.labs_app.ui.OnboardScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class OnboardActivity : BaseLoggedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Labs_APPTheme {
                OnboardScreen(
                    onContinue = {
                        Log.d(lifecycleTag, "Переход в SignInActivity (без данных)")
                        startActivity(Intent(this, SignInActivity::class.java))
                    }
                )
            }
        }
    }
}
