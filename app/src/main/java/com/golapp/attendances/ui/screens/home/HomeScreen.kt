package com.golapp.attendances.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.golapp.attendances.R
import com.golapp.attendances.common.Constants.SHAPE_LARGE
import com.golapp.attendances.common.Constants.SPACER_LARGE
import com.golapp.attendances.common.Constants.SPACER_MEDIUM
import com.golapp.attendances.common.ui.components.HeaderContent
import com.golapp.attendances.ui.theme.GolappAttendancesTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val list = listOf<String>(
        stringResource(R.string.groups_info),
        stringResource(R.string.attendance_info),
        stringResource(R.string.sync_groups),
        stringResource(R.string.action_attendance),
        stringResource(R.string.sync_info),
    )

    Surface(
        modifier = modifier.padding(horizontal = SPACER_MEDIUM),
    ) {
        Column {
            HeaderContent()

            Spacer(modifier = Modifier.height(SPACER_MEDIUM))

            Row(
                modifier = Modifier
                    .padding(horizontal = SPACER_MEDIUM)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(100.dp, 60.dp),
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Image Header Content"
                    )
                    Text(
                        stringResource(R.string.attendances).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(SPACER_MEDIUM))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(list.size) {
                    ItemsText(text = list[it])
                }
            }
        }
    }
}

@Composable
private fun ItemsText(
    modifier: Modifier = Modifier,
    text: String
) {
    Surface(
        modifier = modifier
            .padding(SPACER_MEDIUM)
            .fillMaxWidth(),
        tonalElevation = 1.5.dp,
        shadowElevation = 1.dp,
        shape = CutCornerShape(topEnd = SHAPE_LARGE, bottomStart = SHAPE_LARGE),
    ) {
        Row {
            Column(
                modifier = Modifier
            ) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = modifier.padding(vertical = SPACER_LARGE, horizontal = SPACER_LARGE),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    GolappAttendancesTheme {
        HomeScreen()
    }
}
