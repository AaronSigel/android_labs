package com.example.labs_app.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.labs_app.databinding.FragmentSettingsBinding
import com.example.labs_app.storage.BackupFileManager
import com.example.labs_app.storage.PreferencesDataStoreManager
import com.example.labs_app.storage.SharedPrefsManager
import com.example.labs_app.ui.SettingsScreen
import com.example.labs_app.ui.theme.ThemeFromSettings

class SettingsFragment : Fragment() {

    private val logTag: String get() = "LabsApp/${javaClass.simpleName}"

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: SettingsRepository

    private var pendingFileAction: (() -> Unit)? = null

    private val storagePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.values.all { it }
        Log.d(logTag, "Результат запроса разрешений: granted=$allGranted, result=$result")
        if (allGranted) {
            pendingFileAction?.invoke()
        } else {
            Log.w(logTag, "Разрешения хранилища не выданы")
            viewModel.setError(getString(com.example.labs_app.R.string.settings_permission_required))
        }
        pendingFileAction = null
    }

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(logTag, "onCreate")
        val app = requireContext().applicationContext
        repository = SettingsRepository(
            PreferencesDataStoreManager(app),
            SharedPrefsManager(app),
            BackupFileManager(app)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(logTag, "onViewCreated")
        binding.composeView.setContent {
            ThemeFromSettings {
                SettingsScreen(
                    viewModel = viewModel,
                    onCreateFileRequest = { runWithStoragePermission { viewModel.createOrOverwriteFile() } },
                    onDeleteFileRequest = { runWithStoragePermission { viewModel.deleteExternalFile() } },
                    onRestoreRequest = { runWithStoragePermission { viewModel.restoreFromBackup() } },
                    onNavigateToHome = {
                        Log.d(logTag, "Кнопка «На главную» → popBackStack to Home")
                        findNavController().popBackStack()
                    }
                )
            }
        }
    }

    private fun runWithStoragePermission(action: () -> Unit) {
        if (hasStoragePermission()) {
            Log.d(logTag, "Разрешения уже есть — выполняем действие")
            action()
        } else {
            Log.d(logTag, "Запрос разрешений хранилища перед действием")
            pendingFileAction = action
            permissionLauncher.launch(storagePermissions)
        }
    }

    private fun hasStoragePermission(): Boolean {
        return storagePermissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(logTag, "onDestroyView")
        _binding = null
    }
}
