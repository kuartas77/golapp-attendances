package com.golapp.attendances.feature.groups

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golapp.attendances.R
import com.golapp.attendances.core.common.Constants.SPACER_MEDIUM
import com.golapp.attendances.core.common.scheduleInline
import com.golapp.attendances.core.common.ui.components.AlertDialogSync
import com.golapp.attendances.core.common.ui.components.Loader
import com.golapp.attendances.core.common.ui.components.SearchBar
import com.golapp.attendances.core.common.ui.preview.groupWithClassPreview
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.ui.theme.BrandDefaults
import com.golapp.attendances.ui.theme.GolappAttendancesTheme
import com.golapp.attendances.ui.theme.GolappElevation
import com.golapp.attendances.ui.theme.GolappSpacing
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun GroupsScreen(
    onNavigateBackHome: () -> Unit = {},
    onClickClassDay: (String) -> Unit = {},
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val viewModel: GroupsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Evita capturar una lambda vieja si recomponen
    val showSnackbarLatest = rememberUpdatedState(onShowSnackbar)

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is GroupsUiEffect.ShowSnackbar -> {
                    val performed = showSnackbarLatest.value(
                        effect.message,
                        effect.actionLabel
                    )

                    // ✅ Si el usuario presionó la acción del snackbar
                    if (performed) {
                        when (effect.action) {
                            GroupsUiAction.RetrySync -> viewModel.onEvent(GroupsUiEvent.Retry)
                            null -> Unit
                        }
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.padding(horizontal = GolappSpacing.md),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column {
            Loader(show = uiState.isLoading)

            ListGroups(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onNavigateBackHome = onNavigateBackHome,
                onClickClassDay = onClickClassDay
            )
        }
    }
}


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListGroups(
    modifier: Modifier = Modifier,
    uiState: GroupsUiState = GroupsUiState(),
    onEvent: (GroupsUiEvent) -> Unit = {},
    onNavigateBackHome: () -> Unit = {},
    onClickClassDay: (String) -> Unit = {}
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<GroupWithClassDays>()
    val scope = rememberCoroutineScope()
    val backBehavior = if (navigator.canNavigateBack()) {
        BackNavigationBehavior.PopUntilContentChange
    } else {
        BackNavigationBehavior.PopUntilScaffoldValueChange
    }

    BackHandler(navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack(backBehavior)
        }
    }

    BackHandler(!navigator.canNavigateBack()) {
        onNavigateBackHome()
    }

    ListDetailPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                ListPanelGroups(
                    uiState = uiState,
                    onEvent = onEvent,
                    navigator = navigator
                )
            }
        },
        detailPane = {
            AnimatedPane {
                DetailGroupPanel(navigator = navigator) {
                    onClickClassDay(it)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ListPanelGroups(
    modifier: Modifier = Modifier,
    uiState: GroupsUiState,
    onEvent: (GroupsUiEvent) -> Unit,
    navigator: ThreePaneScaffoldNavigator<GroupWithClassDays>
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val groupWithClassDays = uiState.listGroups

    Column(
        modifier = modifier
            .padding(top = GolappSpacing.xxs, start = GolappSpacing.xs, end = GolappSpacing.xs)
    ) {
        SearchBarSection(uiState = uiState, onEvent = onEvent)
        Spacer(modifier = modifier.height(SPACER_MEDIUM))

        if (groupWithClassDays.isEmpty()) {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = uiState.blockingError ?: stringResource(R.string.no_groups_found)
                )

                if (uiState.blockingError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.retry),
                        modifier = Modifier.clickable { onEvent(GroupsUiEvent.Retry) },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs)
            ) {
                items(
                    count = groupWithClassDays.count(),
                    key = { it }
                ) {
                    val group = groupWithClassDays[it]
                    GroupItem(item = group) {
                        onEvent(GroupsUiEvent.OnSelectGroup(group))
                        scope.launch {
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


}

@Composable
private fun SearchBarSection(
    modifier: Modifier = Modifier,
    uiState: GroupsUiState,
    onEvent: (GroupsUiEvent) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    // Estado del input (tu SearchBar lo requiere)
    val searchState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(uiState.query))
    }

    // ✅ Mantiene el input sincronizado cuando el VM cambia query (ej: OnClearText)
    LaunchedEffect(uiState.query) {
        val current = searchState.value
        if (current.text != uiState.query) {
            searchState.value = current.copy(text = uiState.query)
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(GolappSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            enabled = !uiState.isSyncing,
            onClick = { showDialog = !showDialog }
        ) {
            if (uiState.isSyncing) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_sync),
                    contentDescription = "Sync Groups"
                )
            }
        }

        SearchBar(
            hint = stringResource(id = R.string.groups),
            state = searchState,
            onSearchClicked = { onEvent(GroupsUiEvent.OnSearchGroup(it)) },
            onTextChange = { onEvent(GroupsUiEvent.OnSearchGroup(it)) },
            onClearClick = { onEvent(GroupsUiEvent.OnClearText) },
            cornerShape = MaterialTheme.shapes.medium,
        )
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
    item: GroupWithClassDays,
    onClickItem: (GroupWithClassDays) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clickable { onClickItem(item) },
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = GolappElevation.card),
        colors = BrandDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(GolappSpacing.sm)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,

                    ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(item.group.fullGroup)
                            }
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = modifier.height(GolappSpacing.xs))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.members))
                                append(" ")
                                append(item.group.playerCount.toString())
                            }
                        },
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = modifier.height(GolappSpacing.sm))

                    Row(
                        modifier = modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.id_calendar),
                            contentDescription = "Icon Date",
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = item.group.days.scheduleInline(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(
                        modifier = modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_stopwatch),
                            contentDescription = "Icon Date",
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = item.group.explodeSchedules.scheduleInline(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupsScreenPreview() {
    GolappAttendancesTheme {
        GroupItem(item = groupWithClassPreview())
    }
}
