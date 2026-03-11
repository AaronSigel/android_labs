package com.example.labs_app.ui.onboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.labs_app.databinding.FragmentOnboardBinding
import com.example.labs_app.ui.OnboardScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class OnboardFragment : Fragment() {

    private val logTag: String get() = "LabsApp/${javaClass.simpleName}"

    private var _binding: FragmentOnboardBinding? = null
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
        _binding = FragmentOnboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.setContent {
            Labs_APPTheme {
                OnboardScreen(
                    onContinue = {
                        Log.d(logTag, "Кнопка «Продолжить» → navigate to SignIn")
                        findNavController().navigate(
                            OnboardFragmentDirections.actionOnboardToSignIn(prefill = null)
                        )
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
