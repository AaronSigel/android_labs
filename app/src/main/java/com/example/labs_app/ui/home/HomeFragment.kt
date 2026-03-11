package com.example.labs_app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.labs_app.databinding.FragmentHomeBinding
import com.example.labs_app.ui.HomeScreen
import com.example.labs_app.ui.theme.Labs_APPTheme

class HomeFragment : Fragment() {

    private val logTag: String get() = "LabsApp/${javaClass.simpleName}"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = HomeFragmentArgs.fromBundle(requireArguments())
        val user = args.user
        val username = user?.username
        Log.d(logTag, "onViewCreated: user из Safe Args = ${user?.let { "username=${it.username}, email=${it.email}" } ?: "null"}")

        binding.composeView.setContent {
            Labs_APPTheme {
                HomeScreen(
                    viewModel = viewModel,
                    username = username,
                    onNavigateToOnboard = {
                        Log.d(logTag, "Кнопка «О приложении» → navigate to Onboard")
                        findNavController().navigate(HomeFragmentDirections.actionHomeToOnboard())
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
