package com.golapp.attendances.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golapp.attendances.R
import com.golapp.attendances.common.Constants.SHAPE_LARGE
import com.golapp.attendances.common.Constants.SPACER_LARGE
import com.golapp.attendances.common.Constants.SPACER_MEDIUM
import com.golapp.attendances.common.Constants.SPACER_SMALL
import com.golapp.attendances.common.ui.components.HeaderContent
import com.golapp.attendances.domain.models.Statistics

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(horizontal = SPACER_MEDIUM)
            .verticalScroll(rememberScrollState()),
    ) {
        Column {
            HeaderContent()

            Spacer(modifier = Modifier.height(SPACER_MEDIUM))

            SectionInfo()

        }
    }
}

@Composable
private fun SectionInfo() {

    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listStatistics = uiState.listStatistics
    val textListInfo = listOf<String>(
        stringResource(R.string.groups_info),
        stringResource(R.string.attendance_info),
        stringResource(R.string.sync_groups),
        stringResource(R.string.action_attendance),
        stringResource(R.string.sync_info),
    )

    Row(
        modifier = Modifier
            .padding(horizontal = SPACER_MEDIUM)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.attendances).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    Spacer(modifier = Modifier.height(SPACER_MEDIUM))

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        modifier = Modifier
            .height(180.dp)
            .padding(vertical = SPACER_MEDIUM),
        verticalArrangement = Arrangement.spacedBy(SPACER_SMALL),
        horizontalArrangement = Arrangement.spacedBy(SPACER_SMALL)
    ) {
        items(count = listStatistics.size, key = { it }) {
            val statistics = listStatistics[it]
            StatisticsCard(statistic = statistics)
        }
    }

    Spacer(modifier = Modifier.height(SPACER_MEDIUM))

    textListInfo.forEach { item ->
        ItemsText(text = item)
    }
}

@Composable
private fun ItemsText(
    modifier: Modifier = Modifier,
    text: String
) {
    OutlinedCard(
        Modifier
            .padding(SPACER_SMALL),
        shape = CutCornerShape(topEnd = SHAPE_LARGE, bottomStart = SHAPE_LARGE),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row {
            Column(
                modifier = Modifier
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
    }
}


@Composable
fun StatisticsCard(modifier: Modifier = Modifier, statistic: Statistics) {
    OutlinedCard(
        Modifier
            .size(180.dp)
            .padding(SPACER_SMALL),
        shape = CutCornerShape(topEnd = SHAPE_LARGE, bottomStart = SHAPE_LARGE),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
        ) {
            Column {
                Text(
                    statistic.fullGroup,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        HorizontalDivider(modifier = modifier.padding(start = 12.dp, end = 12.dp))

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp),
        ) {
            Column(modifier = modifier.weight(1f)) {
                Text(
                    stringResource(R.string.taken),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    statistic.attendancesTaken.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(modifier = modifier.weight(.5f)) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    statistic.attendancesTotal.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        HorizontalDivider(modifier = modifier.padding(start = 12.dp, end = 12.dp))

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
        ) {
            Column(modifier = modifier.weight(1f)) {
                Text(
                    stringResource(R.string.pendindg),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    statistic.attendancesNoTaken.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            VerticalDivider(modifier = modifier.padding(start = 12.dp, end = 12.dp))

            Column(modifier = modifier.weight(.5f)) {
                Text(
                    "%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(statistic.avg, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
