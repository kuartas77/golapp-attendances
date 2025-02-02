package com.golapp.attendances.common.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

object GolappRadioButton {
    @Composable
    operator fun invoke(
        title: String,
        value: String,
        selectedOption: String,
        onOptionSelected: (selected: String) -> Unit
    ) {
        val isSelected = (value == selectedOption)
        if (isSelected) onOptionSelected(value)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.selectable(
                selected = isSelected,
                onClick = { onOptionSelected(value) },
                role = Role.RadioButton
            )
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { onOptionSelected(value) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = title,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
