package com.example.labs_app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.labs_app.IntentKeys
import com.example.labs_app.model.User
import com.example.labs_app.ui.SignInScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class SignInActivity : BaseLoggedActivity() {

    private val signUpLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = result.data ?: return@registerForActivityResult

        // Демонстрация двух способов приёма: читаем и логируем оба варианта
        @Suppress("DEPRECATION")
        val userFromObject = data.getSerializableExtra(IntentKeys.USER) as? User
        Log.d(lifecycleTag, "[Способ 1 — объект User] getSerializableExtra(USER): ${userFromObject?.let { "username=${it.username}, email=${it.email}, password=${it.password}" } ?: "null"}")

        val usernameExtra = data.getStringExtra(IntentKeys.USERNAME).orEmpty()
        val emailExtra = data.getStringExtra(IntentKeys.EMAIL).orEmpty()
        val passwordExtra = data.getStringExtra(IntentKeys.PASSWORD).orEmpty()
        Log.d(lifecycleTag, "[Способ 2 — String extras] getStringExtra: USERNAME=$usernameExtra, EMAIL=$emailExtra, PASSWORD=$passwordExtra")

        val user = userFromObject
            ?: if (usernameExtra.isNotEmpty() || emailExtra.isNotEmpty()) {
                User(usernameExtra, emailExtra, passwordExtra)
            } else null
        if (user != null) {
            setContent {
                Labs_APPTheme {
                    SignInScreen(
                        initialUser = user,
                        onLoginSuccess = { username -> navigateToHome(username) },
                        onRegister = { launchSignUp() },
                        onBack = { navigateToOnboard() }
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        @Suppress("DEPRECATION")
        val initialUser = intent.getSerializableExtra(IntentKeys.USER) as? User
            ?: run {
                val username = intent.getStringExtra(IntentKeys.USERNAME).orEmpty()
                val email = intent.getStringExtra(IntentKeys.EMAIL).orEmpty()
                val password = intent.getStringExtra(IntentKeys.PASSWORD).orEmpty()
                if (username.isEmpty() && email.isEmpty()) null
                else User(username, email, password)
            }
        if (initialUser != null) {
            Log.d(lifecycleTag, "Получено из Intent при старте: username=${initialUser.username}, email=${initialUser.email}, password=${initialUser.password}")
        }
        setContent {
            Labs_APPTheme {
                SignInScreen(
                    initialUser = initialUser,
                    onLoginSuccess = { navigateToHome(it) },
                    onRegister = { launchSignUp() },
                    onBack = { navigateToOnboard() }
                )
            }
        }
    }

    private fun launchSignUp() {
        signUpLauncher.launch(Intent(this, SignUpActivity::class.java))
    }

    private fun navigateToHome(username: String) {
        Log.d(lifecycleTag, "Передача в HomeActivity: USERNAME=$username")
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(IntentKeys.USERNAME, username)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToOnboard() {
        startActivity(Intent(this, OnboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        })
        finish()
    }
}
