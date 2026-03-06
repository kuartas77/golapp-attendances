package com.golapp.attendances.common.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.golapp.attendances.ui.theme.BrandDefaults

@Preview
@Composable
fun AserButtonPreview() {
    CustomButton(text = "Tester Button", onClick = {})
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = isEnabled,
        colors = BrandDefaults.primaryButtonColors()
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}