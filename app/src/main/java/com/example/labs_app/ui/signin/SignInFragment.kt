package com.example.labs_app.ui.signin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.labs_app.model.SignInPrefill
import com.example.labs_app.model.User
import com.example.labs_app.ui.SignInScreen
import com.example.labs_app.ui.signup.KEY_SIGN_UP_RESULT
import com.example.labs_app.ui.signup.KEY_SIGN_UP_USER
import com.example.labs_app.ui.theme.Labs_APPTheme
import com.example.labs_app.util.getParcelableCompat

private const val ARG_PREFILL = "prefill"

class SignInFragment : Fragment() {

    private val tag: String get() = "LabsApp/${javaClass.simpleName}"
    private val prefillState = mutableStateOf<SignInPrefill?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        prefillState.value = arguments?.getParcelableCompat<SignInPrefill>(ARG_PREFILL)
        Log.d(tag, "onCreateView: prefill из arguments = ${prefillState.value?.let { "username=${it.username}, email=${it.email}" } ?: "null"}")
        return ComposeView(requireContext()).apply {
            setContent {
                Labs_APPTheme {
                    SignInScreen(
                        initialUser = prefillState.value?.let {
                            User(it.username, it.email, it.password)
                        },
                        onLoginSuccess = { username ->
                            val user = prefillState.value?.let {
                                User(it.username, it.email, it.password)
                            } ?: User(username, "", "")
                            Log.d(tag, "Вход успешен → navigateToHome(user=username=${user.username})")
                            (activity as? com.example.labs_app.ui.main.MainActivity)
                                ?.navigateToHome(user)
                        },
                        onRegister = {
                            Log.d(tag, "Кнопка «Регистрация» → navigateToSignUp()")
                            (activity as? com.example.labs_app.ui.main.MainActivity)
                                ?.navigateToSignUp()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(tag, "onViewCreated: подписка на FragmentResult KEY_SIGN_UP_RESULT")
        parentFragmentManager.setFragmentResultListener(KEY_SIGN_UP_RESULT, viewLifecycleOwner) { _, bundle ->
            bundle.getParcelableCompat<User>(KEY_SIGN_UP_USER)?.let { user ->
                Log.d(tag, "[Передача данных] Получен результат из SignUpFragment: username=${user.username}, email=${user.email}, nickname=${user.nickname}")
                prefillState.value = SignInPrefill(user.username, user.email, user.password)
            }
        }
    }

    companion object {
        fun newInstance(prefilledData: SignInPrefill? = null): SignInFragment {
            return SignInFragment().apply {
                arguments = Bundle().apply {
                    prefilledData?.let { putParcelable(ARG_PREFILL, it) }
                }
            }
        }
    }
}
