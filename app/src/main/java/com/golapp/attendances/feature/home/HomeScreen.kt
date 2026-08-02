package com.golapp.attendances.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golapp.attendances.R
import com.golapp.attendances.domain.models.Statistics
import com.golapp.attendances.ui.theme.BrandDefaults
import com.golapp.attendances.ui.theme.GolappElevation
import com.golapp.attendances.ui.theme.GolappSpacing

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = GolappSpacing.md)
                .verticalScroll(rememberScrollState()),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column {
                SectionInfo(onLogout)
            }
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

    Spacer(modifier = Modifier.height(GolappSpacing.xxs))

    Text(
        text = stringResource(R.string.attendance_statistics),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(GolappSpacing.xs))

    when {
        uiState.isLoading -> StatisticsLoading()
        uiState.error != null && listStatistics.isEmpty() -> StatisticsMessage(
            message = stringResource(R.string.statistics_load_error),
            actionLabel = stringResource(R.string.retry),
            onAction = viewModel::fetchStatistics,
        )
        listStatistics.isEmpty() -> StatisticsMessage(
            message = stringResource(R.string.no_statistics_available),
        )
        else -> Column(verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs)) {
            listStatistics.forEach { statistic ->
                StatisticsCard(statistic = statistic)
            }
        }
    }
}

@Composable
private fun StatisticsLoading() {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = BrandDefaults.elevatedCardColors(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GolappSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(GolappSpacing.md, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
            Text(
                text = stringResource(R.string.loading_statistics),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatisticsMessage(
    message: String,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = BrandDefaults.elevatedCardColors(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GolappSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (actionLabel != null) {
                TextButton(onClick = onAction) {
                    Text(actionLabel)
                }
            }
        }
    }
}


@Composable
fun StatisticsCard(modifier: Modifier = Modifier, statistic: Statistics) {
    val progress = if (statistic.attendancesTotal > 0) {
        statistic.attendancesTaken.toFloat() / statistic.attendancesTotal
    } else {
        0f
    }.coerceIn(0f, 1f)

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = BrandDefaults.elevatedCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = GolappElevation.card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GolappSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(GolappSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = statistic.fullGroup,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = statistic.avg,
                    modifier = Modifier.padding(horizontal = GolappSpacing.sm, vertical = GolappSpacing.xs),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = GolappSpacing.md))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GolappSpacing.sm, vertical = GolappSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatisticMetric(
                label = stringResource(R.string.taken),
                value = statistic.attendancesTaken,
                valueColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(modifier = Modifier.height(48.dp))
            StatisticMetric(
                label = stringResource(R.string.pending),
                value = statistic.attendancesNoTaken,
                valueColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(modifier = Modifier.height(48.dp))
            StatisticMetric(
                label = stringResource(R.string.total),
                value = statistic.attendancesTotal,
                valueColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = GolappSpacing.md, end = GolappSpacing.md, bottom = GolappSpacing.md),
            verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs),
        ) {
            Text(
                text = stringResource(R.string.attendance_completion),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
private fun StatisticMetric(
    label: String,
    value: Int,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                textAlign = TextAlign.Center,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
