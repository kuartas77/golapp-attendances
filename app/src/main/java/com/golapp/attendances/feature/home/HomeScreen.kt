package com.golapp.attendances.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golapp.attendances.R
import com.golapp.attendances.core.common.Constants.SPACER_SMALL
import com.golapp.attendances.domain.models.Statistics
import com.golapp.attendances.ui.theme.BrandDefaults
import com.golapp.attendances.ui.theme.GolappElevation
import com.golapp.attendances.ui.theme.GolappSpacing

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .padding(horizontal = GolappSpacing.md)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column {
            SectionInfo(onLogout)
        }
    }
}

@Composable
private fun SectionInfo(onLogout: () -> Unit) {

    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listStatistics = uiState.listStatistics

    LaunchedEffect(viewModel) {
        viewModel.start()
    }

    LaunchedEffect(uiState.isLoggedIn) {
        if (!uiState.isLoggedIn) {
            onLogout()
        }
    }

    val textListInfo = listOf(
        stringResource(R.string.groups_info),
        stringResource(R.string.attendance_info),
        stringResource(R.string.sync_groups),
        stringResource(R.string.action_attendance),
        stringResource(R.string.sync_info),
    )

//    Row(
//        modifier = Modifier
//            .padding(top = GolappSpacing.xxs, start = GolappSpacing.xs, end = GolappSpacing.xs)
//            .fillMaxWidth(),
//        horizontalArrangement = Arrangement.Start
//    ) {
//        Column(
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.Start
//        ) {
//            Text(
//                stringResource(R.string.attendances),
//                fontWeight = FontWeight.Bold,
//                style = MaterialTheme.typography.headlineSmall,
//                color = MaterialTheme.colorScheme.onBackground
//            )
//        }
//    }

    Spacer(modifier = Modifier.height(GolappSpacing.xxs))

    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        modifier = Modifier
            .height(168.dp)
            .padding(vertical = GolappSpacing.xs),
        verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs),
        horizontalArrangement = Arrangement.spacedBy(GolappSpacing.xs)
    ) {
        items(count = listStatistics.size, key = { it }) {
            val statistics = listStatistics[it]
            StatisticsCard(statistic = statistics)
        }
    }

    Spacer(modifier = Modifier.height(GolappSpacing.sm))

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
        modifier = Modifier
            .padding(vertical = GolappSpacing.xs)
            .fillMaxWidth(),
        colors = BrandDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = GolappElevation.card),
    ) {
        Row {
            Column(
                modifier = Modifier
            ) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = modifier.padding(vertical = GolappSpacing.sm, horizontal = GolappSpacing.md),
                )
            }
        }
    }
}


@Composable
fun StatisticsCard(modifier: Modifier = Modifier, statistic: Statistics) {
    OutlinedCard(
        Modifier
            .size(250.dp)
            .padding(SPACER_SMALL),
        colors = BrandDefaults.elevatedCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = GolappElevation.card)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .padding(top = 12.dp, start = 14.dp, end = 14.dp)
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
                    style = MaterialTheme.typography.titleSmall,
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
                    style = MaterialTheme.typography.titleSmall,
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
                    stringResource(R.string.pending),
                    style = MaterialTheme.typography.titleSmall,
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
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(statistic.avg, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
