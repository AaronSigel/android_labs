package com.example.labs_app.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import com.example.labs_app.model.SignInPrefill
import com.example.labs_app.model.User
import com.example.labs_app.ui.home.HomeFragment
import com.example.labs_app.ui.onboard.OnboardFragment
import com.example.labs_app.ui.signin.SignInFragment
import com.example.labs_app.ui.signup.SignUpFragment

/**
 * Единственная Activity приложения. Хостит все экраны в виде Fragment.
 * Контейнер для фрагментов создаётся программно (без XML).
 */
class MainActivity : FragmentActivity() {

    private val tag: String get() = "LabsApp/${javaClass.simpleName}"

    /** Generated id контейнера фрагментов. */
    val fragmentContainerId: Int by lazy { View.generateViewId() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate, savedInstanceState=${savedInstanceState != null}")
        enableEdgeToEdge()
        val container = FragmentContainerView(this).apply {
            id = fragmentContainerId
        }
        setContentView(container)
        if (savedInstanceState == null) {
            Log.d(tag, "Первый запуск → navigateToOnboard()")
            navigateToOnboard()
        }
    }

    fun navigateToOnboard() {
        Log.d(tag, "navigateToOnboard()")
        supportFragmentManager.commit {
            replace(fragmentContainerId, OnboardFragment())
            setReorderingAllowed(true)
        }
    }

    fun navigateToSignIn(prefilledData: SignInPrefill? = null) {
        Log.d(tag, "navigateToSignIn(prefilledData=${prefilledData?.let { "username=${it.username}, email=${it.email}" } ?: "null"})")
        supportFragmentManager.commit {
            replace(fragmentContainerId, SignInFragment.newInstance(prefilledData))
            addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    fun navigateToSignUp() {
        Log.d(tag, "navigateToSignUp()")
        supportFragmentManager.commit {
            replace(fragmentContainerId, SignUpFragment())
            addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    fun navigateToHome(user: User? = null) {
        Log.d(tag, "navigateToHome(user=${user?.let { "username=${it.username}, email=${it.email}" } ?: "null"})")
        supportFragmentManager.commit {
            replace(fragmentContainerId, HomeFragment.newInstance(user))
            addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    fun navigateBack() {
        val count = supportFragmentManager.backStackEntryCount
        Log.d(tag, "navigateBack(), backStackEntryCount=$count")
        if (count > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }
}
