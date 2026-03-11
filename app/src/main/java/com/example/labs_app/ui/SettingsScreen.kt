package com.example.labs_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.labs_app.R
import com.example.labs_app.storage.formatFileLastModified
import com.example.labs_app.ui.settings.SettingsUiState
import com.example.labs_app.ui.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onCreateFileRequest: () -> Unit,
    onDeleteFileRequest: () -> Unit,
    onRestoreRequest: () -> Unit,
    onNavigateToHome: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = SettingsUiState())

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        onNavigateToHome?.let { navigate ->
            TextButton(onClick = navigate) {
                Text(stringResource(R.string.settings_go_to_home))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        OutlinedTextField(
            value = state.userEmail,
            onValueChange = viewModel::updateUserEmail,
            label = { Text(stringResource(R.string.settings_user_email)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_notifications),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = state.notificationsEnabled,
                onCheckedChange = viewModel::updateNotificationsEnabled
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_dark_theme),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = state.darkTheme,
                onCheckedChange = viewModel::updateDarkTheme
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.backupFileName,
            onValueChange = viewModel::updateBackupFileName,
            label = { Text(stringResource(R.string.settings_backup_file_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.settings_external_file_status),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (state.externalFileInfo.exists) {
                    Text(
                        text = stringResource(R.string.settings_file_exists, state.externalFileInfo.fileName),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.settings_file_path, state.externalFileInfo.fullPath),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.settings_file_size, state.externalFileInfo.sizeBytes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.settings_file_modified, formatFileLastModified(state.externalFileInfo.lastModifiedTime)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = stringResource(R.string.settings_file_absent),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.settings_internal_backup_status),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (state.internalBackupInfo.exists) {
                        stringResource(R.string.settings_backup_exists)
                    } else {
                        stringResource(R.string.settings_backup_absent)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        state.errorMessage?.let { msg ->
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = { viewModel.saveSettings() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(Modifier.height(24.dp).fillMaxWidth(0.2f))
            } else {
                Text(stringResource(R.string.settings_save))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onCreateFileRequest,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.canCreateFile
        ) {
            if (state.isExporting) {
                CircularProgressIndicator(Modifier.height(24.dp).fillMaxWidth(0.2f))
            } else {
                Text(stringResource(R.string.settings_create_file))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onDeleteFileRequest,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.canDeleteFile
        ) {
            if (state.isDeleting) {
                CircularProgressIndicator(Modifier.height(24.dp).fillMaxWidth(0.2f))
            } else {
                Text(stringResource(R.string.settings_delete_file))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onRestoreRequest,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.canRestore
        ) {
            if (state.isRestoring) {
                CircularProgressIndicator(Modifier.height(24.dp).fillMaxWidth(0.2f))
            } else {
                Text(stringResource(R.string.settings_restore))
            }
        }
    }
}
