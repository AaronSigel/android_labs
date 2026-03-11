package com.example.labs_app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.labs_app.IntentKeys
import com.example.labs_app.getMockChatList
import com.example.labs_app.ui.HomeScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class HomeActivity : BaseLoggedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val username = intent.getStringExtra(IntentKeys.USERNAME).orEmpty()
        if (username.isNotEmpty()) {
            Log.d(lifecycleTag, "Получено из SignInActivity: USERNAME=$username")
        }
        setContent {
            Labs_APPTheme {
                HomeScreen(
                    chatList = getMockChatList(),
                    username = username.ifEmpty { null },
                    onNavigateToOnboard = {
                        startActivity(Intent(this, OnboardActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
