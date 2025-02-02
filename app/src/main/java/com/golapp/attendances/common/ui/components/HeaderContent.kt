package com.golapp.attendances.common.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.golapp.attendances.R


@Composable
fun HeaderContent(
    modifier: Modifier = Modifier,
    schoolName: String = "Golapp",
    userName: String = "Juan"
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {

                Text(text = userName)

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = schoolName)
            }

            Image(
                modifier = Modifier.size(100.dp, 52.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Image Header Content"
            )
        }
    }
}
