package com.golapp.attendances.ui.screens.groups.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.golapp.attendances.R
import com.golapp.attendances.common.Constants.SHAPE_MEDIUM
import com.golapp.attendances.common.ui.components.groupWithClassPreview
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.ui.theme.GolappAttendancesTheme


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldScope.DetailGroupPanel(
    navigator: ThreePaneScaffoldNavigator<GroupWithClassDays>,
    navigateToAttendances: (String) -> Unit
) {
    AnimatedPane {
        navigator.currentDestination?.content?.let { group ->
            DetailScreen(
                group = group,
                backButton = {
                    AnimatedVisibility(
                        visible = navigator.canNavigateBack()
                    ) {
                        IconButton(
                            onClick = {
                                navigator.navigateBack()
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
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = {
                    Text(
                        text = group.fullGroup,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = backButton
            )
        },
    ) { paddingValues ->

        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.members))
                                append(" ")
                            }
                            append(group.playerCount.toString())
                        },
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = modifier.height(4.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.schedules))
                                append(" ")
                            }
                            append(group.explodeSchedules)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = modifier.height(4.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.days))
                                append(" ")
                            }
                            append(group.days)
                        },
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = modifier.height(4.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.trainings))
                                append(" ")
                            }
                            append(group.classDays.size.toString())
                        },
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

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
                        color = MaterialTheme.colorScheme.onPrimary,
                        shape = MaterialTheme.shapes.large
                    )
                ) {
                    FlowRow(
                        modifier = Modifier.padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
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

@Composable
private fun ItemDay(
    item: ClassDay,
    navigateToAttendances: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp)
            .clickable { navigateToAttendances(item.classDayId) },
        shape = CutCornerShape(SHAPE_MEDIUM),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .size(90.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.id_calendar),
                contentDescription = "calendar",
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = item.date.toString(),
                modifier = Modifier,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = item.day,
                modifier = Modifier,
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
