package com.golapp.attendances.common.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.golapp.attendances.R

@Composable
fun AlertDialogSync(
    showDialog: Boolean = false,
    textBody: Int = R.string.sync_info,
    onConfirm: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    var openDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showDialog) {
        if (showDialog) openDialog = true
    }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_sync),
                    contentDescription = "Sync Attendances"
                )
            },
            title = { Text(text = stringResource(R.string.sync)) },
            text = {
                Text(stringResource(textBody))
            },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissRequest()
                }) { Text(stringResource(R.string.dismiss)) }
            }
        )
    }
}

@Preview
@Composable
private fun AlertDialogSyncPreview() {
    AlertDialogSync(showDialog = true)
}