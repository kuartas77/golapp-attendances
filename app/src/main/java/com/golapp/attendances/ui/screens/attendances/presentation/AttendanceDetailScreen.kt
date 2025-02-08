package com.golapp.attendances.ui.screens.attendances.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.golapp.attendances.R
import com.golapp.attendances.common.ui.components.GolappRadioButton
import com.golapp.attendances.common.ui.components.attendanceWithPlayerPreview
import com.golapp.attendances.common.ui.getMonthName
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.ui.theme.GolappAttendancesTheme

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldScope.DetailPanelAttendance(
    modifier: Modifier = Modifier,
    navigator: ThreePaneScaffoldNavigator<AttendanceWithPlayer>,
    viewModel: AttendancesViewModel = hiltViewModel()
) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedPane {

        navigator.currentDestination?.content?.let { item ->
            Crossfade(
                targetState = item,
                label = "Detail Pane",
            ) { attendance ->
                DetailScreen(
                    modifier = modifier,
                    attendance = attendance,
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
                    onTakeAttendance = {
                        viewModel.onEvent(AttendancesUiEvent.OnTakeAttendance(it))
                    }
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
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DetailScreen(
    modifier: Modifier = Modifier,
    attendance: AttendanceWithPlayer,
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
                        text = attendance.player.fullNames,
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
                modifier = Modifier.fillMaxSize(),
            ) {
                Column {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
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
                            Spacer(modifier = modifier.height(4.dp))
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(stringResource(R.string.names))
                                    }
                                    append(" ")
                                    append(attendance.player.names)
                                },
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = modifier.height(4.dp))
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(stringResource(R.string.lastNames))
                                    }
                                    append(" ")
                                    append(attendance.player.lastNames)
                                },
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = modifier.height(4.dp))
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
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = modifier.height(4.dp))
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(stringResource(R.string.month))
                                    }
                                    append(" ")
                                    append(getMonthName(attendance.month))
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Surface(
                            shape = MaterialTheme.shapes.large,
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

                    Text(
                        text = stringResource(R.string.action_attendance),
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        FlowRow(
                            modifier = Modifier
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
