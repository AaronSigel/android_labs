package com.example.labs_app.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.labs_app.model.User
import com.example.labs_app.ui.SignUpScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

/** Ключ для передачи результата регистрации в SignInFragment. */
const val KEY_SIGN_UP_RESULT = "sign_up_result"
const val KEY_SIGN_UP_USER = "user"

class SignUpFragment : Fragment() {

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
                    SignUpScreen(
                        onSuccessToSignIn = { user ->
                            Log.d(tag, "[Передача данных] setFragmentResult(KEY_SIGN_UP_RESULT): username=${user.username}, email=${user.email}, nickname=${user.nickname}")
                            parentFragmentManager.setFragmentResult(
                                KEY_SIGN_UP_RESULT,
                                Bundle().apply { putParcelable(KEY_SIGN_UP_USER, user) }
                            )
                            Log.d(tag, "navigateBack() → возврат в SignInFragment")
                            (activity as? com.example.labs_app.ui.main.MainActivity)?.navigateBack()
                        },
                        onBack = {
                            Log.d(tag, "Кнопка «Назад» → navigateBack()")
                            (activity as? com.example.labs_app.ui.main.MainActivity)?.navigateBack()
                        }
                    )
                }
            }
        }
    }
}
