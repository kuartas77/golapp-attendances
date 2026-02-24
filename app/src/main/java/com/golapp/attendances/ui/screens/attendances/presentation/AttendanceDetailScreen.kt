package com.golapp.attendances.ui.screens.attendances.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.golapp.attendances.R
import com.golapp.attendances.common.ui.components.GolappRadioButton
import com.golapp.attendances.common.ui.preview.attendanceWithPlayerPreview
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.ui.theme.GolappAttendancesTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DetailPanelAttendance(
    modifier: Modifier = Modifier,
    navigator: ThreePaneScaffoldNavigator<AttendanceWithPlayer>,
    uiState: AttendancesUiState,
    onTakeAttendance: (AttendanceWithPlayer) -> Unit
) {

    val scope = rememberCoroutineScope()

    navigator.currentDestination?.contentKey?.let { selectedItem ->
        Crossfade(
            targetState = selectedItem,
            label = "Detail Pane",
        ) { attendance ->
            DetailScreen(
                modifier = modifier,
                attendance = selectedItem,
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
                onTakeAttendance = onTakeAttendance,
                uiState = uiState

            )
        }
    } ?: Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = {
            Text(
                text = stringResource(R.string.no_attendance_selected),
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DetailScreen(
    modifier: Modifier = Modifier,
    attendance: AttendanceWithPlayer,
    uiState: AttendancesUiState = AttendancesUiState(),
    backButton: @Composable () -> Unit = {},
    onTakeAttendance: (AttendanceWithPlayer) -> Unit = {},
) {
    val attendancesList = remember { getListOfAttendance() }
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(attendance.value) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = {
                    Text(
                        text = stringResource(R.string.asistencia),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = { backButton() },
            )
        },
    ) { paddingValues ->

        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Header(modifier, attendance)

                Column(modifier = modifier.padding(12.dp)) {

                    Text(
                        text = stringResource(R.string.action_attendance),
                        modifier = modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic
                    )

                    Card(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        FlowRow(
                            modifier = modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalArrangement = Arrangement.Center,
                            maxItemsInEachRow = 3
                        ) {
                            attendancesList.forEach { item ->
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    GolappRadioButton(
                                        item.title,
                                        item.value,
                                        selectedOption.toString()
                                    ) { it ->
                                        onTakeAttendance(attendance.copy(value = it))
                                        onOptionSelected(it)
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    modifier: Modifier,
    attendance: AttendanceWithPlayer,
) {
    val imageRequest = ImageRequest.Builder(LocalContext.current).data(attendance.player.photoUrl)
        .build()
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column {
                Text(
                    attendance.player.fullNames,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(start = 12.dp, end = 12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(20.dp)
            ) {
                Text(
                    stringResource(R.string.unique_code),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    attendance.player.uniqueCode,
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider(modifier = Modifier)

                Text(
                    stringResource(R.string.category),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    attendance.player.category,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.size(width = 100.dp, height = 100.dp)
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

        HorizontalDivider(modifier = Modifier.padding(start = 12.dp, end = 12.dp))
    }
}

@PreviewLightDark
@Composable
private fun DetailScreenPreview() {
    GolappAttendancesTheme {
        DetailScreen(
            attendance = attendanceWithPlayerPreview(),
            backButton = {
                AnimatedVisibility(
                    visible = true
                ) {
                    IconButton(
                        onClick = {},
                        content = {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    )
                }
            }
        )
    }
}
