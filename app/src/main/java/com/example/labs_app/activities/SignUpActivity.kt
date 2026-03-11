package com.example.labs_app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.labs_app.IntentKeys
import com.example.labs_app.model.User
import com.example.labs_app.ui.SignUpScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class SignUpActivity : BaseLoggedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Labs_APPTheme {
                SignUpScreen(
                    onSuccessToSignIn = { user ->
                        // Демонстрация двух способов передачи: оба пишем в Intent и логируем все поля
                        Log.d(lifecycleTag, "[Способ 1 — объект User] putExtra(USER, User): username=${user.username}, email=${user.email}, password=${user.password}")
                        Log.d(lifecycleTag, "[Способ 2 — String extras] putExtra: USERNAME=${user.username}, EMAIL=${user.email}, PASSWORD=${user.password}")
                        val resultIntent = Intent().apply {
                            putExtra(IntentKeys.USER, user)
                            putExtra(IntentKeys.USERNAME, user.username)
                            putExtra(IntentKeys.EMAIL, user.email)
                            putExtra(IntentKeys.PASSWORD, user.password)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
}
