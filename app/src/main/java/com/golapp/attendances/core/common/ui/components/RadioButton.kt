package com.golapp.attendances.core.common.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.golapp.attendances.ui.theme.GolappSize

object GolappRadioButton {
    @Composable
    operator fun invoke(
        title: String,
        value: String,
        selectedOption: String,
        onOptionSelected: (selected: String) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.heightIn(min = GolappSize.minTouchTarget)
        ) {
            RadioButton(
                selected = value == selectedOption,
                onClick = { onOptionSelected(value) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.outline
                )
            )
            Text(
                text = title,
                modifier = Modifier
                    .clickable(onClick = { onOptionSelected(value) })
                    .padding(start = 4.dp, end = 4.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
