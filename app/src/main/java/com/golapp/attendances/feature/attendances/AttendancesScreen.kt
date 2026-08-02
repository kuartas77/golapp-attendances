package com.golapp.attendances.feature.attendances

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.golapp.attendances.R
import com.golapp.attendances.core.common.Constants.SPACER_SMALL
import com.golapp.attendances.core.common.ui.components.AlertDialogSync
import com.golapp.attendances.core.common.ui.components.Loader
import com.golapp.attendances.core.common.ui.components.SearchBar
import com.golapp.attendances.core.common.ui.preview.attendanceWithPlayerPreview
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.ui.theme.GolappAttendancesTheme
import com.golapp.attendances.ui.theme.GolappElevation
import com.golapp.attendances.ui.theme.GolappSpacing
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AttendancesScreen(
    classDayId: String,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: AttendancesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showSnackbarLatest = rememberUpdatedState(onShowSnackbar)

    LaunchedEffect(classDayId) {
        viewModel.setClassDayId(classDayId)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is AttendancesUiEffect.ShowSnackbar -> {
                    val performed = showSnackbarLatest.value(effect.message, effect.actionLabel)
                    if (performed) {
                        when (val action = effect.action) {
                            AttendancesUiAction.RetrySync -> viewModel.onEvent(AttendancesUiEvent.SyncAttendances)
                            AttendancesUiAction.RetryLoad -> viewModel.onEvent(AttendancesUiEvent.RetryLoad)
                            is AttendancesUiAction.RetryTake -> viewModel.onEvent(
                                AttendancesUiEvent.OnTakeAttendance(action.attendance)
                            )

                            null -> Unit
                        }
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.padding(horizontal = GolappSpacing.md),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Loader(show = uiState.isLoading)
            ListAttendances(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onTakeAttendance = { viewModel.onEvent(AttendancesUiEvent.OnTakeAttendance(it)) }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListAttendances(
    modifier: Modifier = Modifier,
    uiState: AttendancesUiState = AttendancesUiState(),
    onEvent: (AttendancesUiEvent) -> Unit = {},
    onTakeAttendance: (AttendanceWithPlayer) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val useInlineTaking = windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass ==
        WindowWidthSizeClass.COMPACT
    val navigator = rememberListDetailPaneScaffoldNavigator<AttendanceWithPlayer>(
        isDestinationHistoryAware = false
    )

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

    ListDetailPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                ListPanelAttendances(
                    uiState = uiState,
                    onEvent = onEvent,
                    navigator = navigator,
                    useInlineTaking = useInlineTaking,
                    onTakeAttendance = onTakeAttendance,
                )
            }
        },
        detailPane = {
            DetailPanelAttendance(
                navigator = navigator,
                uiState = uiState,
                onTakeAttendance = onTakeAttendance
            )
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ListPanelAttendances(
    modifier: Modifier = Modifier,
    uiState: AttendancesUiState = AttendancesUiState(),
    onEvent: (AttendancesUiEvent) -> Unit,
    navigator: ThreePaneScaffoldNavigator<AttendanceWithPlayer>,
    useInlineTaking: Boolean,
    onTakeAttendance: (AttendanceWithPlayer) -> Unit,
) {
    val listState = rememberLazyListState()
    val attendances = uiState.listAttendances
    val scope = rememberCoroutineScope()
    var expandedPlayerId by rememberSaveable { mutableStateOf<Int?>(null) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(GolappSpacing.xs)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBarSection(uiState = uiState, onEvent = onEvent)
        }
        Spacer(modifier = modifier.height(SPACER_SMALL))
        if (attendances.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                content = {
                    Text(
                        uiState.blockingError ?: stringResource(R.string.no_attendances_found)
                    )
                }
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs)
            ) {
                items(
                    count = attendances.count(),
                    key = { it }
                ) {
                    val attendance = attendances[it]
                    AttendanceItem(
                        attendance = attendance,
                        selected = uiState.selectedAttendance?.playerId == attendance.playerId,
                        inlineTaking = useInlineTaking,
                        expanded = useInlineTaking && expandedPlayerId == attendance.playerId,
                        onTakeAttendance = { updatedAttendance ->
                            onTakeAttendance(updatedAttendance)
                            expandedPlayerId = null
                        },
                    ) {
                        if (useInlineTaking) {
                            expandedPlayerId = if (expandedPlayerId == attendance.playerId) {
                                null
                            } else {
                                attendance.playerId
                            }
                        } else {
                            onEvent(AttendancesUiEvent.OnSelectAttendance(attendance))
                            scope.launch {
                                navigator.navigateTo(
                                    ListDetailPaneScaffoldRole.Detail,
                                    attendance
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
private fun SearchBarSection(
    modifier: Modifier = Modifier,
    uiState: AttendancesUiState = AttendancesUiState(),
    onEvent: (AttendancesUiEvent) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    // Estado del input (tu SearchBar lo requiere)
    val searchState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(uiState.query))
    }

    // Mantiene el input sincronizado cuando el VM cambia query
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
            onClick = { showDialog = !showDialog },
            content = {
                Icon(
                    painter = painterResource(R.drawable.ic_sync),
                    contentDescription = "Sync Attendances"
                )
            }
        )
        SearchBar(
            hint = stringResource(
                id = R.string.title_attendances_p,
                uiState.classDaySelected?.monthName.toString(),
                uiState.classDaySelected?.date.toString(),
                uiState.classDaySelected?.day.toString()
            ),
            state = searchState,
            onSearchClicked = { onEvent(AttendancesUiEvent.OnSearchAttendance(it)) },
            onTextChange = { onEvent(AttendancesUiEvent.OnSearchAttendance(it)) },
            onClearClick = { onEvent(AttendancesUiEvent.OnClearText) },
            cornerShape = MaterialTheme.shapes.medium,
        )
    }

    AnimatedVisibility(visible = showDialog) {
        AlertDialogSync(
            showDialog = showDialog,
            onConfirm = {
                showDialog = false
                onEvent(AttendancesUiEvent.SyncAttendances)
            },
            onDismissRequest = { showDialog = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AttendanceItem(
    modifier: Modifier = Modifier,
    attendance: AttendanceWithPlayer,
    selected: Boolean = false,
    inlineTaking: Boolean = false,
    expanded: Boolean = false,
    onTakeAttendance: (AttendanceWithPlayer) -> Unit = {},
    onClickItem: () -> Unit = {}
) {
    val attendancesList = remember { getListOfAttendance() }
    var attendanceValue = ""

    val attendanceFind = attendancesList.find { it.value == attendance.value }
    attendanceFind?.let {
        attendanceValue = it.title
    } ?: run {
        attendanceValue = stringResource(R.string.take_attendance)
    }

    val imageRequest = ImageRequest.Builder(LocalContext.current).data(attendance.player.photoUrl)
        .build()

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clickable { onClickItem() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = GolappElevation.card),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(GolappSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(GolappSpacing.sm),
            ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.size(64.dp),
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = stringResource(R.string.player_photo),
                    placeholder = painterResource(R.drawable.user),
                    error = painterResource(R.drawable.user),
                    contentScale = ContentScale.Crop,
                )
            }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs),
                ) {
                Text(
                    text = attendance.player.fullNames,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(
                        R.string.player_code_and_category,
                        attendance.player.uniqueCode,
                        attendance.player.category,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = attendanceStatusColor(attendance.value),
                        contentColor = MaterialTheme.colorScheme.surface,
                    ) {
                        Text(
                            text = attendanceValue,
                            modifier = Modifier.padding(
                                horizontal = GolappSpacing.sm,
                                vertical = GolappSpacing.xs,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            if (inlineTaking) {
                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = GolappSpacing.md,
                                end = GolappSpacing.md,
                                bottom = GolappSpacing.md,
                            ),
                        verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs),
                    ) {
                        Text(
                            text = stringResource(R.string.select_attendance_status),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(GolappSpacing.xs),
                            verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs),
                        ) {
                            attendancesList.forEach { option ->
                                val optionSelected = option.value == attendance.value
                                FilterChip(
                                    selected = optionSelected,
                                    onClick = {
                                        onTakeAttendance(attendance.copy(value = option.value))
                                    },
                                    label = { Text(option.title) },
                                    leadingIcon = {
                                        Surface(
                                            modifier = Modifier.size(10.dp),
                                            shape = MaterialTheme.shapes.extraLarge,
                                            color = attendanceStatusColor(option.value),
                                        ) {}
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    ),
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
internal fun attendanceStatusColor(value: String?) = when (value) {
    "1" -> MaterialTheme.colorScheme.primary
    "2" -> MaterialTheme.colorScheme.error
    "3" -> MaterialTheme.colorScheme.tertiary
    "4" -> MaterialTheme.colorScheme.secondary
    "5" -> MaterialTheme.colorScheme.inverseSurface
    else -> MaterialTheme.colorScheme.outline
}

@Preview
@Composable
private fun AttendanceItemPreview() {
    GolappAttendancesTheme {
        AttendanceItem(attendance = attendanceWithPlayerPreview())
    }
}
