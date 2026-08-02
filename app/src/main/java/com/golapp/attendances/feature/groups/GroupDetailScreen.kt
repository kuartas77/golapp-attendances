package com.golapp.attendances.feature.groups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.golapp.attendances.R
import com.golapp.attendances.core.common.scheduleInline
import com.golapp.attendances.core.common.ui.preview.groupWithClassPreview
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.ui.theme.BrandDefaults
import com.golapp.attendances.ui.theme.GolappElevation
import com.golapp.attendances.ui.theme.GolappSpacing
import com.golapp.attendances.ui.theme.GolappAttendancesTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DetailGroupPanel(
    navigator: ThreePaneScaffoldNavigator<GroupWithClassDays>,
    navigateToAttendances: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    navigator.currentDestination?.contentKey?.let { group ->
        DetailScreen(
            group = group,
            backButton = {
                AnimatedVisibility(
                    visible = navigator.canNavigateBack()
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                navigator.navigateBack()
                            }
                        },
                        content = {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    )
                }
            },
            navigateToAttendances = navigateToAttendances
        )
    } ?: Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = {
            Text(
                text = stringResource(R.string.no_group_selected),
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
            )
        }
    )

}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun DetailScreen(
    modifier: Modifier = Modifier,
    group: GroupWithClassDays,
    backButton: @Composable () -> Unit = {},
    navigateToAttendances: (String) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                colors = BrandDefaults.topAppBarColors(),
                title = {
                    Text(
                        text = stringResource(R.string.info_group),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = backButton
            )
        },
    ) { paddingValues ->

        Surface(
            modifier = modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(GolappSpacing.md),
                verticalArrangement = Arrangement.spacedBy(GolappSpacing.md),
            ) {
                GroupOverview(group)

                if (group.classDays.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs)) {
                    Text(
                            text = stringResource(R.string.pick_training_date),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(
                                R.string.available_dates_month,
                                group.classDays.first().monthName,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.large,
                        ) {
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(GolappSpacing.sm),
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = GolappSpacing.xs,
                                    alignment = Alignment.CenterHorizontally,
                                ),
                                verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs),
                            ) {
                                group.classDays.forEach { classDay ->
                                    ItemDay(
                                        item = classDay,
                                        navigateToAttendances = navigateToAttendances,
                                    )
                                }
                            }
                        }
                    }
                } else {
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.no_training_dates),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(GolappSpacing.lg),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupOverview(groupWithClassDays: GroupWithClassDays) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = BrandDefaults.elevatedCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = GolappElevation.card),
    ) {
        Column(
            modifier = Modifier.padding(GolappSpacing.md),
            verticalArrangement = Arrangement.spacedBy(GolappSpacing.md),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GolappSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_team),
                        contentDescription = null,
                        modifier = Modifier.padding(GolappSpacing.xs),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.nombre_del_grupo),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = groupWithClassDays.group.fullGroup,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(GolappSpacing.sm)) {
                GroupMetric(
                    value = groupWithClassDays.group.playerCount.toString(),
                    label = stringResource(R.string.members),
                    modifier = Modifier.weight(1f),
                )
                GroupMetric(
                    value = groupWithClassDays.classDays.size.toString(),
                    label = stringResource(R.string.trainings),
                    modifier = Modifier.weight(1f),
                )
            }

            DetailInfoRow(
                iconRes = R.drawable.id_calendar,
                label = stringResource(R.string.training_days),
                value = groupWithClassDays.group.days.scheduleInline(),
            )
            DetailInfoRow(
                iconRes = R.drawable.ic_stopwatch,
                label = stringResource(R.string.training_schedule),
                value = groupWithClassDays.group.explodeSchedules.scheduleInline(),
            )
        }
    }
}

@Composable
private fun GroupMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(GolappSpacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun DetailInfoRow(iconRes: Int, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(GolappSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ItemDay(
    modifier: Modifier = Modifier,
    item: ClassDay,
    navigateToAttendances: (String) -> Unit
) {
    Card(
        modifier = modifier
            .wrapContentSize()
            .clickable { navigateToAttendances(item.classDayId) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = GolappElevation.card),
    ) {
        Column(
            modifier = modifier
                .size(width = 94.dp, height = 100.dp)
                .padding(GolappSpacing.xs),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.id_calendar),
                contentDescription = null,
                modifier = modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = item.date.toString(),
                modifier = modifier,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = item.day,
                modifier = modifier,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun GroupItemPreview() {
    GolappAttendancesTheme {
        DetailScreen(
            group = groupWithClassPreview(),
            backButton = {
                IconButton(
                    onClick = {},
                    content = {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                )
            },
            navigateToAttendances = {}
        )
    }
}
