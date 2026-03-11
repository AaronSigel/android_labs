package com.example.labs_app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.labs_app.getMockChatList
import com.example.labs_app.model.User
import com.example.labs_app.ui.HomeScreen
import com.example.labs_app.ui.theme.Labs_APPTheme
import com.example.labs_app.util.getParcelableCompat

private const val ARG_USER = "user"

class HomeFragment : Fragment() {

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
        val user = arguments?.getParcelableCompat<User>(ARG_USER)
        val username = user?.username
        Log.d(tag, "onCreateView: user из arguments = ${user?.let { "username=${it.username}, email=${it.email}" } ?: "null"}")
        return ComposeView(requireContext()).apply {
            setContent {
                Labs_APPTheme {
                    HomeScreen(
                        chatList = getMockChatList(),
                        username = username,
                        onNavigateToOnboard = {
                            Log.d(tag, "Кнопка «О приложении» → navigateToOnboard()")
                            (activity as? com.example.labs_app.ui.main.MainActivity)?.navigateToOnboard()
                        }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance(user: User? = null): HomeFragment {
            return HomeFragment().apply {
                arguments = Bundle().apply {
                    user?.let { putParcelable(ARG_USER, it) }
                }
            }
        }
    }
}
