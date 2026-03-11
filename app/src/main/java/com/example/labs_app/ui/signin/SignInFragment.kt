package com.example.labs_app.ui.signin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.labs_app.databinding.FragmentSignInBinding
import com.example.labs_app.model.User
import com.example.labs_app.ui.SignInScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class SignInFragment : Fragment() {

    private val logTag: String get() = "LabsApp/${javaClass.simpleName}"

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = SignInFragmentArgs.fromBundle(requireArguments())
        val prefill = args.prefill
        Log.d(logTag, "onViewCreated: prefill из Safe Args = ${prefill?.let { "username=${it.username}, email=${it.email}" } ?: "null"}")

        binding.composeView.setContent {
            Labs_APPTheme {
                SignInScreen(
                    initialUser = prefill?.let {
                        User(it.username, it.email, it.password)
                    },
                    onLoginSuccess = { username ->
                        val user = prefill?.let {
                            User(it.username, it.email, it.password)
                        } ?: User(username, "", "")
                        Log.d(logTag, "Вход успешен → navigate to Home")
                        findNavController().navigate(
                            SignInFragmentDirections.actionSignInToHome(user = user)
                        )
                    },
                    onRegister = {
                        Log.d(logTag, "Кнопка «Регистрация» → navigate to SignUp")
                        findNavController().navigate(SignInFragmentDirections.actionSignInToSignUp())
                    },
                    onBack = {
                        Log.d(logTag, "Кнопка «Назад» → popBackStack")
                        findNavController().popBackStack()
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
