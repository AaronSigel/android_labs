package com.example.labs_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.labs_app.ui.SignUpScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Labs_APPTheme {
                SignUpScreen(
                    onSuccessToSignIn = {
                        startActivity(Intent(this, SignInActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP })
                        finish()
                    },
                    onSuccessToHome = {
                        startActivity(Intent(this, HomeActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
                        finish()
                    },
                    onBack = {
                        startActivity(Intent(this, SignInActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
