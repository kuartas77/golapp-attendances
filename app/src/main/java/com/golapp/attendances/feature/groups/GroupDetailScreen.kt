package com.golapp.attendances.feature.groups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
                    .padding(horizontal = GolappSpacing.sm),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Header(modifier, group)

                HorizontalDivider(modifier = modifier.padding(start = 12.dp, end = 12.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {

                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.pick_date))
                            append(" ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(group.classDays.first().monthName)
                            }
                        },

                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic
                    )

                    Column(
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.large
                        )
                    ) {
                        FlowRow(
                            modifier = Modifier.padding(GolappSpacing.xs),
                            horizontalArrangement = Arrangement.spacedBy(GolappSpacing.xs),
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
            }
        }
    }

}

@Composable
private fun Header(modifier: Modifier, groupWithClassDays: GroupWithClassDays) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(GolappSpacing.sm)
    ) {
        Column {
            Text(
                stringResource(R.string.nombre_del_grupo),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(groupWithClassDays.group.fullGroup, style = MaterialTheme.typography.bodyMedium)
        }
    }

    HorizontalDivider(modifier = modifier.padding(start = 12.dp, end = 12.dp))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(GolappSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = modifier.weight(1f)) {
            Text(
                stringResource(R.string.days),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                groupWithClassDays.group.days.scheduleInline(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    HorizontalDivider(modifier = modifier.padding(start = 12.dp, end = 12.dp))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(GolappSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = modifier.weight(1f)) {
            Text(
                stringResource(R.string.schedules),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                groupWithClassDays.group.explodeSchedules.scheduleInline(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    HorizontalDivider(modifier = modifier.padding(start = 12.dp, end = 12.dp))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(GolappSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = modifier.weight(1f)) {
            Text(
                stringResource(R.string.trainings),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                groupWithClassDays.classDays.size.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(modifier = modifier.weight(1f)) {
            Text(
                stringResource(R.string.members),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                groupWithClassDays.group.playerCount.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
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
            .padding(2.dp)
            .clickable { navigateToAttendances(item.classDayId) },
        shape = MaterialTheme.shapes.small,
        colors = BrandDefaults.elevatedCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = GolappElevation.card),
    ) {
        Column(
            modifier = modifier
                .size(86.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.id_calendar),
                contentDescription = "calendar",
                modifier = modifier.size(22.dp),
            )
            Text(
                text = item.date.toString(),
                modifier = modifier,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = item.day,
                modifier = modifier,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
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
