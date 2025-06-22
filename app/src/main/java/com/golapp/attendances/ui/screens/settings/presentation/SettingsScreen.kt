package com.golapp.attendances.ui.screens.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.golapp.attendances.R
import com.golapp.attendances.common.Constants.SPACER_MEDIUM
import com.golapp.attendances.common.Constants.SPACER_MEDIUM_LARGE
import com.golapp.attendances.common.ui.components.HeaderContent
import com.golapp.attendances.ui.screens.settings.presentation.components.SettingsItem
import com.golapp.attendances.ui.theme.GolappAttendancesTheme

@Composable
fun SettingsScreen(onLogout: () -> Unit) {

    Surface(
        modifier = Modifier.padding(horizontal = SPACER_MEDIUM)
    ) {
        Column {
            HeaderContent()

            Configuration(onLogout)
        }
    }
}

@Composable
private fun Configuration(onLogout: () -> Unit) {
    Spacer(modifier = Modifier.height(SPACER_MEDIUM))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SPACER_MEDIUM_LARGE),
    ) {
        Text(
            text = stringResource(R.string.title_settings),
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
        SettingsItem(
            text = stringResource(R.string.log_out),
            Icons.AutoMirrored.Filled.ExitToApp,
            onLogout
        )
    }
    Spacer(modifier = Modifier.height(SPACER_MEDIUM))
}


@Preview
@Composable
private fun PreviewSettingsScreen() {
    GolappAttendancesTheme {
        SettingsScreen(onLogout = {})
    }
}
