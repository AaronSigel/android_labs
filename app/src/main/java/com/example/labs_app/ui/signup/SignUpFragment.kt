package com.example.labs_app.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.labs_app.databinding.FragmentSignUpBinding
import com.example.labs_app.model.SignInPrefill
import com.example.labs_app.ui.SignUpScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class SignUpFragment : Fragment() {

    private val logTag: String get() = "LabsApp/${javaClass.simpleName}"

    private var _binding: FragmentSignUpBinding? = null
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
        Log.d(logTag, "onCreateView")
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.setContent {
            Labs_APPTheme {
                SignUpScreen(
                    onSuccessToSignIn = { user ->
                        val prefill = SignInPrefill(user.username, user.email, user.password)
                        Log.d(logTag, "[Safe Args] Переход в SignIn с данными: username=${user.username}, email=${user.email}")
                        findNavController().navigate(
                            SignUpFragmentDirections.actionSignUpToSignIn(prefill = prefill)
                        )
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
