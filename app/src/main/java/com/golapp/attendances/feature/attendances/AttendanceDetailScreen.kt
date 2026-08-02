package com.golapp.attendances.feature.attendances

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.golapp.attendances.core.common.ui.preview.attendanceWithPlayerPreview
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.ui.theme.BrandDefaults
import com.golapp.attendances.ui.theme.GolappElevation
import com.golapp.attendances.ui.theme.GolappSpacing
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
        val currentAttendance = uiState.selectedAttendance ?: selectedItem

        Crossfade(
            targetState = currentAttendance,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(
    modifier: Modifier = Modifier,
    attendance: AttendanceWithPlayer,
    uiState: AttendancesUiState = AttendancesUiState(),
    backButton: @Composable () -> Unit = {},
    onTakeAttendance: (AttendanceWithPlayer) -> Unit = {},
) {
    val attendancesList = remember { getListOfAttendance() }
    var selectedOption by remember(attendance.id) { mutableStateOf(attendance.value) }

    LaunchedEffect(attendance.value) {
        selectedOption = attendance.value
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                colors = BrandDefaults.topAppBarColors(),
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
                modifier = modifier
                    .fillMaxSize()
                    .padding(GolappSpacing.md),
                verticalArrangement = Arrangement.spacedBy(GolappSpacing.md),
            ) {
                PlayerHeader(attendance)

                Column(verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs)) {
                    Text(
                        text = stringResource(R.string.select_attendance_status),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.attendance_saved_automatically),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(GolappSpacing.xs)) {
                        attendancesList.forEach { item ->
                            AttendanceStatusOption(
                                title = item.title,
                                value = item.value,
                                selected = item.value == selectedOption,
                                onSelected = {
                                    selectedOption = item.value
                                    onTakeAttendance(attendance.copy(value = item.value))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerHeader(
    attendance: AttendanceWithPlayer,
) {
    val imageRequest = ImageRequest.Builder(LocalContext.current).data(attendance.player.photoUrl)
        .build()
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = BrandDefaults.elevatedCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = GolappElevation.card),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GolappSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(GolappSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.size(96.dp),
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
                    attendance.player.fullNames,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(
                        R.string.player_code_and_category,
                        attendance.player.uniqueCode,
                        attendance.player.category,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AttendanceStatusOption(
    title: String,
    value: String,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    val statusColor = attendanceStatusColor(value)
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected),
        shape = MaterialTheme.shapes.medium,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GolappSpacing.sm, vertical = GolappSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(GolappSpacing.sm),
        ) {
            Surface(
                modifier = Modifier.size(12.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = statusColor,
            ) {}
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
            RadioButton(selected = selected, onClick = onSelected)
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
