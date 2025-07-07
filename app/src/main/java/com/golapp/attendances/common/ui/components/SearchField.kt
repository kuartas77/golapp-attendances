package com.golapp.attendances.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golapp.attendances.R
import com.golapp.attendances.common.Constants.SPACER_LARGE
import com.golapp.attendances.ui.theme.GolappAttendancesTheme


@Preview
@Composable
fun SearchFieldPreview() {
    GolappAttendancesTheme {
        SearchField(searchDisplay = "", onSearchDisplayChanged = {}, searchLabel = "")
    }
}

@Preview()
@Composable
private fun Basic() {
    GolappAttendancesTheme {
        SearchBar(
            hint = "grupos",
            onSearchClicked = {},
            onTextChange = {},
            cornerShape = RoundedCornerShape(20.dp),
            state = remember { mutableStateOf(TextFieldValue()) }
        )
    }
}

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    searchDisplay: String,
    onSearchDisplayChanged: (String) -> Unit,
    searchLabel: String
) {
    var textValue by rememberSaveable { mutableStateOf(searchDisplay) }
    val keyboardController = LocalSoftwareKeyboardController.current
    //val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    Surface(
        color = Color.Transparent,
        shadowElevation = 8.dp,
        tonalElevation = 5.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        TextField(
            value = textValue,
            onValueChange = { textValue = it },
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "search icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            label = {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.search_bar_text))
                        append(" ")
                        append(searchLabel)

                    },
                    style = MaterialTheme.typography.labelSmall
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            maxLines = 1,
            modifier = modifier
                .fillMaxWidth()
                .padding(2.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchDisplayChanged(textValue)
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            )
        )
    }
}

@Composable
fun SearchBar(
    hint: String = "",
    modifier: Modifier = Modifier.padding(end = SPACER_LARGE),
    isEnabled: (Boolean) = true,
    height: Dp = 40.dp,
    elevation: Dp = 0.dp,
    cornerShape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = Color.White,
    state: MutableState<TextFieldValue>,
    onSearchClicked: (String) -> Unit = {},
    onTextChange: (String) -> Unit = {},
    onClearClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .shadow(elevation = elevation, shape = cornerShape)
            .background(color = backgroundColor, shape = cornerShape),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        BasicTextField(
            modifier = modifier
                .weight(5f)
                .fillMaxWidth()
                .focusable()
                .padding(horizontal = 24.dp),
            value = state.value,
            onValueChange = {
                state.value = it
                onTextChange(it.text)
            },
            enabled = isEnabled,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            decorationBox = { innerTextField ->
                if (state.value.text.isEmpty()) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.search_bar_text))
                            append(" ")
                            append(hint)
                        },
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                innerTextField()
            },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearchClicked(state.value.text) }),
            singleLine = true
        )

        Box(
            modifier = modifier
                .weight(1f)
                .size(40.dp)
                .background(color = Color.Transparent, shape = CircleShape),
        ) {
            if (state.value.text.isNotEmpty()) {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .clickable {
                            if (state.value.text.isNotEmpty()) {
                                state.value = TextFieldValue(text = "")
                                onClearClick()
                            }
                        },
                    painter = painterResource(id = R.drawable.ic_clear),
                    contentDescription = stringResource(id = R.string.search_bar_text),
                    tint = MaterialTheme.colorScheme.primary,
                )
            } else {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .clickable(
                            onClick = { onSearchClicked(state.value.text) }
                        ),
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.search_bar_text),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
