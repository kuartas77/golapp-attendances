package com.golapp.attendances.common.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.golapp.attendances.R

@Composable
fun ScheduleTimeContent(
    modifier: Modifier = Modifier,
    date: String = "Sunday, 12 June",
    time: String = "11.00 - 12.00 PM"
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Content(icon = R.drawable.id_calendar, title = date)

        Content(icon = R.drawable.ic_stopwatch, title = time)
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    icon: Int,
    title: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Icon Date",
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = title, style = MaterialTheme.typography.bodySmall)
    }
}


@Preview(showBackground = true)
@Composable
private fun ScheduleTimeContentPreview() {
    ScheduleTimeContent()
}
