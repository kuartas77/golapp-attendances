package com.golapp.attendances.ui.screens.attendances.presentation

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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.golapp.attendances.R
import com.golapp.attendances.common.Constants.SHAPE_LARGE
import com.golapp.attendances.common.Constants.SPACER_MEDIUM
import com.golapp.attendances.common.Constants.SPACER_SMALL
import com.golapp.attendances.common.ui.components.HeaderContent
import com.golapp.attendances.common.ui.components.SearchBar
import com.golapp.attendances.common.ui.components.attendanceWithPlayerPreview
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.ui.theme.GolappAttendancesTheme

@Composable
fun AttendancesScreen(
    modifier: Modifier = Modifier,
    viewModel: AttendancesViewModel = hiltViewModel()
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
                ListAttendances(
                    uiState = uiState,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListAttendances(
    modifier: Modifier = Modifier,
    uiState: AttendancesUiState = AttendancesUiState(),
    onEvent: (AttendancesUiEvent) -> Unit = {}
) {

    val navigator = rememberListDetailPaneScaffoldNavigator<AttendanceWithPlayer>()

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
            ListPanelAttendances(
                uiState = uiState,
                onEvent = onEvent,
                navigator = navigator
            )
        },
        detailPane = {
            DetailPanelAttendance(navigator = navigator)
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ThreePaneScaffoldScope.ListPanelAttendances(
    modifier: Modifier = Modifier,
    uiState: AttendancesUiState = AttendancesUiState(),
    onEvent: (AttendancesUiEvent) -> Unit,
    navigator: ThreePaneScaffoldNavigator<AttendanceWithPlayer>
) {
    val listState = rememberLazyListState()
    val attendances = uiState.listAttendances
    var showDialog by remember { mutableStateOf(false) }

    AnimatedPane {
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
                    onSearchClicked = { onEvent(AttendancesUiEvent.OnSearchAttendance(it)) },
                    onTextChange = { onEvent(AttendancesUiEvent.OnSearchAttendance(it)) },
                    cornerShape = MaterialTheme.shapes.medium,
                )
            }
            Spacer(modifier = modifier.height(SPACER_SMALL))
            if (attendances.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = { Text(text = stringResource(R.string.no_attendances_found)) }
                )
            }
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
                        navigator.navigateTo(
                            ListDetailPaneScaffoldRole.Detail,
                            attendance
                        )
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
                // TODO: Sync attendances
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

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .height(120.dp)
            .padding(top = 8.dp)
            .clickable { onClickItem() },
        shape = CutCornerShape(topEnd = SHAPE_LARGE),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                Spacer(modifier = modifier.height(SPACER_MEDIUM))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.names))
                        }
                        append(" ")
                        append(attendance.player.fullNames)
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = modifier.height(SPACER_MEDIUM))
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

                SuggestionChip(
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
                    icon = {
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
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(width = 100.dp, height = 140.dp)
            ) {

                AsyncImage(
                    model = attendance.player.photoUrl,
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

@Preview(showBackground = true)
@Composable
private fun AttendanceItemPreview() {
    GolappAttendancesTheme {
        AttendanceItem(attendance = attendanceWithPlayerPreview())
    }
}

@Composable
fun AlertDialogSync(
    showDialog: Boolean = false,
    onConfirm: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    var openDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showDialog) {
        if (showDialog) openDialog = true
    }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_sync),
                    contentDescription = "Sync Attendances"
                )
            },
            title = { Text(text = stringResource(R.string.sync)) },
            text = {
                Text(stringResource(R.string.sync_info))
            },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissRequest()
                }) { Text(stringResource(R.string.dismiss)) }
            }
        )
    }
}

@Preview
@Composable
private fun AlertDialogSyncPreview() {
    GolappAttendancesTheme {
        AlertDialogSync()
    }
}
