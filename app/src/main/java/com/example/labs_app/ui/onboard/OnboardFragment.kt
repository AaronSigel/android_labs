package com.example.labs_app.ui.onboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.labs_app.ui.OnboardScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class OnboardFragment : Fragment() {

    private val tag: String get() = "LabsApp/${javaClass.simpleName}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(tag, "onCreateView")
        return ComposeView(requireContext()).apply {
            setContent {
                Labs_APPTheme {
                    OnboardScreen(
                        onContinue = {
                            Log.d(tag, "Кнопка «Продолжить» → navigateToSignIn()")
                            (activity as? com.example.labs_app.ui.main.MainActivity)?.navigateToSignIn()
                        }
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        Log.d(tag, "onDestroyView")
        super.onDestroyView()
    }
}
