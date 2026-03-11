package com.example.labs_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.labs_app.ui.SignInScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Labs_APPTheme {
                SignInScreen(
                    onLoginSuccess = {
                        startActivity(Intent(this, HomeActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
                        finish()
                    },
                    onRegister = {
                        startActivity(Intent(this, SignUpActivity::class.java))
                    },
                    onBack = {
                        startActivity(Intent(this, OnboardActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP })
                        finish()
                    }
                )
            }
        }
    }
}
