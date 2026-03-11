package com.example.labs_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.labs_app.ui.OnboardScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class OnboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Labs_APPTheme {
                OnboardScreen(
                    onContinue = {
                        startActivity(Intent(this, SignInActivity::class.java))
                    }
                )
            }
        }
    }
}
