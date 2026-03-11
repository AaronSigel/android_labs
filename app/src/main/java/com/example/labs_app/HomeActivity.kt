package com.example.labs_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.labs_app.ui.HomeScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Labs_APPTheme {
                HomeScreen(
                    chatList = getMockChatList(),
                    onNavigateToOnboard = { startActivity(Intent(this, OnboardActivity::class.java)) }
                )
            }
        }
    }
}
