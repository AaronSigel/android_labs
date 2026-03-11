package com.example.labs_app.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.labs_app.domain.model.Character

private const val EMPTY_PLACEHOLDER = "—"

private fun String.orPlaceholder(): String = if (isBlank()) EMPTY_PLACEHOLDER else this
private fun List<String>.orPlaceholder(): String = if (isEmpty()) EMPTY_PLACEHOLDER else joinToString(", ")

/**
 * Карточка персонажа с полями: name, culture, born, titles, aliases, playedBy.
 * Пустые строки и списки отображаются как "—".
 */
@Composable
fun CharacterCard(
    character: Character,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LabelValue("Имя", character.name.orPlaceholder())
            LabelValue("Культура", character.culture.orPlaceholder())
            LabelValue("Рождение", character.born.orPlaceholder())
            LabelValue("Титулы", character.titles.orPlaceholder())
            LabelValue("Прозвища", character.aliases.orPlaceholder())
            LabelValue("В сериале", character.playedBy.orPlaceholder())
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.outline
    )
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
