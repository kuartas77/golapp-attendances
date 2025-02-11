package com.golapp.attendances.ui.screens.groups.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golapp.attendances.R
import com.golapp.attendances.common.Constants.SHAPE_LARGE
import com.golapp.attendances.common.Constants.SPACER_LARGE
import com.golapp.attendances.common.Constants.SPACER_MEDIUM
import com.golapp.attendances.common.Constants.SPACER_SMALL
import com.golapp.attendances.common.ui.components.AlertDialogSync
import com.golapp.attendances.common.ui.components.HeaderContent
import com.golapp.attendances.common.ui.components.ScheduleTimeContent
import com.golapp.attendances.common.ui.components.SearchBar
import com.golapp.attendances.common.ui.components.groupWithClassPreview
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.ui.theme.GolappAttendancesTheme


@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupsViewModel = hiltViewModel(),
    onClickClassDay: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier.padding(horizontal = SPACER_MEDIUM),
    ) {
        Column {
            HeaderContent()

            Spacer(modifier = Modifier.height(SPACER_SMALL))

            if (uiState.isLoading) {
                Column(
                    modifier = modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = { CircularProgressIndicator() }
                )
            } else {
                ListGroups(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onClickClassDay = onClickClassDay
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListGroups(
    modifier: Modifier = Modifier,
    uiState: GroupsUiState = GroupsUiState(),
    onEvent: (GroupsUiEvent) -> Unit = {},
    onClickClassDay: (String) -> Unit = {}
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<GroupWithClassDays>()
    val backBehavior = if (navigator.canNavigateBack()) {
        BackNavigationBehavior.PopUntilContentChange
    } else {
        BackNavigationBehavior.PopUntilScaffoldValueChange
    }

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack(backBehavior)
    }

    ListDetailPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            ListPanelGroups(
                uiState = uiState,
                onEvent = onEvent,
                navigator = navigator
            )
        },
        detailPane = {
            DetailGroupPanel(navigator = navigator) {
                onClickClassDay(it)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ThreePaneScaffoldScope.ListPanelGroups(
    modifier: Modifier = Modifier,
    uiState: GroupsUiState,
    onEvent: (GroupsUiEvent) -> Unit,
    navigator: ThreePaneScaffoldNavigator<GroupWithClassDays>
) {
    val listState = rememberLazyListState()
    val groups = uiState.listGroups
    var showDialog by remember { mutableStateOf(false) }

    AnimatedPane {
        Column(
            modifier = modifier
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showDialog = !showDialog },
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.ic_sync),
                            contentDescription = "Sync Groups"
                        )
                    }
                )
                SearchBar(
                    hint = stringResource(id = R.string.groups),
                    onSearchClicked = { onEvent(GroupsUiEvent.OnSearchGroup(it)) },
                    onTextChange = { onEvent(GroupsUiEvent.OnSearchGroup(it)) },
                    cornerShape = MaterialTheme.shapes.medium,
                )
            }
            Spacer(modifier = modifier.height(SPACER_SMALL))

            if (uiState.listGroups.isEmpty()) {
                Column(
                    modifier = modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = { Text(text = stringResource(R.string.no_groups_found)) }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = modifier.fillMaxSize()
                ) {
                    items(
                        count = groups.count(),
                        key = { it }
                    ) {
                        val group = groups[it]
                        GroupItem(group = group) {
                            onEvent(GroupsUiEvent.OnSelectGroup(group))
                            navigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                group
                            )
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(visible = showDialog) {
        AlertDialogSync(
            showDialog = showDialog,
            onConfirm = {
                showDialog = false
                onEvent(GroupsUiEvent.SyncGroups)
            },
            textBody = R.string.sync_info_groups,
            onDismissRequest = { showDialog = false }
        )
    }
}

@Composable
private fun GroupItem(
    modifier: Modifier = Modifier,
    group: GroupWithClassDays,
    onClickItem: (GroupWithClassDays) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .height(120.dp)
            .padding(top = 8.dp)
            .clickable { onClickItem(group) },
        shape = CutCornerShape(topEnd = SHAPE_LARGE, bottomStart = SHAPE_LARGE),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(2f),
                verticalArrangement = Arrangement.Center,

                ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(group.fullGroup)
                        }
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = modifier.height(SPACER_SMALL))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.members))
                            append(" ")
                            append(group.playerCount.toString())
                        }
                    },
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = modifier.height(SPACER_LARGE))
                ScheduleTimeContent(
                    date = group.days,
                    time = group.explodeSchedules
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupsScreenPreview() {
    GolappAttendancesTheme {
        GroupItem(group = groupWithClassPreview())
    }
}
