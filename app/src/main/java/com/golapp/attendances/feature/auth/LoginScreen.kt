package com.golapp.attendances.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golapp.attendances.R
import com.golapp.attendances.core.common.Constants.SHAPE_SMALL
import com.golapp.attendances.core.common.ui.components.CustomButton
import com.golapp.attendances.core.common.ui.components.CustomTextField
import com.golapp.attendances.ui.theme.GolappElevation
import com.golapp.attendances.ui.theme.GolappSpacing
import com.golapp.attendances.ui.theme.GolappAttendancesTheme

@Composable
fun LoginScreen(
    onDetectLogin: () -> Unit = {}
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.onEvent(AuthUiEvent.Start)
    }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onDetectLogin()
        }
    }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Image(
                painter = painterResource(id = R.drawable.soccer_field),
                contentDescription = "Soccer Field",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.08f
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(GolappSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Form(uiState = uiState, onEvent = viewModel::onEvent)
                }
            }
        }
    }
}

@Composable
private fun Form(
    modifier: Modifier = Modifier,
    uiState: AuthUiState,
    onEvent: (AuthUiEvent) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier
            .wrapContentHeight()
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .padding(SHAPE_SMALL),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = GolappElevation.card,
        shadowElevation = GolappElevation.raised,
    ) {
        Column(
            modifier = modifier.padding(vertical = GolappSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(92.dp)
                    .fillMaxWidth()
                    .padding(horizontal = GolappSpacing.xl)
            )

            CustomTextField.Email(
                value = uiState.email,
                onValueChange = { onEvent(AuthUiEvent.EmailChanged(it)) },
                contentDescription = stringResource(R.string.input_email),
                label = stringResource(R.string.input_email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GolappSpacing.lg),
                errorMessage = uiState.errorEmail?.asString(),
                isEnabled = uiState.isLoading.not(),
                keyboardActions = KeyboardActions(onAny = {
                    focusManager.moveFocus(FocusDirection.Next)
                }),
                leadingIcon = Icons.Outlined.Email
            )

            CustomTextField.Password(
                value = uiState.password,
                onValueChange = { onEvent(AuthUiEvent.PasswordChanged(it)) },
                contentDescription = stringResource(R.string.input_password),
                label = stringResource(R.string.input_password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GolappSpacing.lg),
                errorMessage = uiState.errorPassword?.asString(),
                isEnabled = uiState.isLoading.not(),
                keyboardActions = KeyboardActions(onAny = {
                    focusManager.clearFocus()
                }),
            )

            CustomButton(
                text = stringResource(R.string.login_button),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GolappSpacing.lg)
                    .padding(top = GolappSpacing.sm, bottom = GolappSpacing.xs),
                isEnabled = uiState.isLoading.not(),
                onClick = { onEvent(AuthUiEvent.LoginClicked) }
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = GolappSpacing.lg, vertical = GolappSpacing.xs)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AuthenticationScreenPreview() {
    GolappAttendancesTheme {
        Form(uiState = AuthUiState())
    }
}
