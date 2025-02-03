package com.golapp.attendances.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golapp.attendances.common.Constants.SPACER_LARGE
import com.golapp.attendances.common.Constants.SPACER_MEDIUM
import com.golapp.attendances.common.Constants.SPACER_SMALL
import com.golapp.attendances.common.ui.components.HeaderContent
import com.golapp.attendances.ui.theme.GolappAttendancesTheme
import com.golapp.attendances.R

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val list = listOf<String>(
        stringResource(R.string.groups_info),
        stringResource(R.string.attendance_info),
        stringResource(R.string.sync_groups),
        stringResource(R.string.action_attendance),
        stringResource(R.string.sync_info),
    )

    Surface(
        modifier = modifier.padding(horizontal = SPACER_LARGE),
    ) {
        Column {
            HeaderContent()

            Spacer(modifier = Modifier.height(SPACER_SMALL))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(128.dp),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 12.dp,
                    end = 12.dp,
                    bottom = 12.dp
                )
            ) {
                items(list.size) { index ->
                    ItemsText(text = list[index])
                }
            }
        }
    }
}

@Composable
private fun ItemsText(
    modifier: Modifier = Modifier,
    text: String
) {
    Surface(
        modifier = modifier.padding(SPACER_MEDIUM).fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.5.dp,
        shadowElevation = 1.dp
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(vertical = SPACER_LARGE, horizontal = SPACER_LARGE),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    GolappAttendancesTheme {
        HomeScreen()
    }
}
