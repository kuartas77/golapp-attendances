package com.golapp.attendances.ui.screens.attendances

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.golapp.attendances.R
import com.golapp.attendances.common.Constants.SPACER_MEDIUM
import com.golapp.attendances.common.Constants.SPACER_SMALL
import com.golapp.attendances.common.ui.components.AlertDialogSync
import com.golapp.attendances.common.ui.components.Loader
import com.golapp.attendances.common.ui.components.SearchBar
import com.golapp.attendances.common.ui.preview.attendanceWithPlayerPreview
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.ui.theme.GolappAttendancesTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AttendancesScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: AttendancesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showSnackbarLatest = rememberUpdatedState(onShowSnackbar)

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

    Surface(modifier = Modifier.padding(horizontal = SPACER_MEDIUM)) {
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
                    navigator = navigator
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
    navigator: ThreePaneScaffoldNavigator<AttendanceWithPlayer>
) {
    val listState = rememberLazyListState()
    val attendances = uiState.listAttendances
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
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
                modifier = modifier.fillMaxSize()
            ) {
                items(
                    count = attendances.count(),
                    key = { it }
                ) {
                    val attendance = attendances[it]
                    AttendanceItem(attendance = attendance) {
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

    // ✅ Mantiene el input sincronizado cuando el VM cambia query (ej: OnClearText)
    LaunchedEffect(uiState.query) {
        val current = searchState.value
        if (current.text != uiState.query) {
            searchState.value = current.copy(text = uiState.query)
        }
    }

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

@Composable
private fun AttendanceItem(
    modifier: Modifier = Modifier,
    attendance: AttendanceWithPlayer,
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .height(120.dp)
            .padding(top = 8.dp)
            .clickable { onClickItem() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(2f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.unique_code))
                        }
                        append(" ")
                        append(attendance.player.uniqueCode)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.category))
                        }
                        append(" ")
                        append(attendance.player.category)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = modifier.height(SPACER_MEDIUM))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.names))
                        }
                        append(" ")
                        append(attendance.player.names)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.lastNames))
                        }
                        append(" ")
                        append(attendance.player.lastNames)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall
                )

                AssistChip(
                    modifier = modifier.wrapContentHeight(),
                    onClick = { onClickItem() },
                    enabled = true,
                    label = {
                        Text(
                            text = attendanceValue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.id_calendar),
                            contentDescription = "calendar",
                            modifier = Modifier.size(12.dp),
                        )
                    },
                    shape = MaterialTheme.shapes.small
                )
            }

            Surface(
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.size(width = 100.dp, height = 140.dp)
            ) {

                AsyncImage(
                    model = imageRequest,
                    contentDescription = stringResource(R.string.player_photo),
                    placeholder = painterResource(R.drawable.user),
                    error = painterResource(R.drawable.user),
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                )
            }
        }
    }
}

@Preview()
@Composable
private fun AttendanceItemPreview() {
    GolappAttendancesTheme {
        AttendanceItem(attendance = attendanceWithPlayerPreview())
    }
}
