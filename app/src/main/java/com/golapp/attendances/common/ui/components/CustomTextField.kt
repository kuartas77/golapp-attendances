package com.golapp.attendances.common.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.golapp.attendances.R
import com.golapp.attendances.common.Constants.SHAPE_SMALL


object CustomTextField {
    @Composable
    operator fun invoke(
        modifier: Modifier = Modifier,
        value: String,
        contentDescription: String,
        onValueChange: (String) -> Unit,
        label: String = "",
        placeholder: String = "",
        errorMessage: String? = null,
        leadingIcon: ImageVector? = null,
        isPassword: Boolean = false,
        isEnabled: Boolean = true,
        keyboardOptions: KeyboardOptions = KeyboardOptions(),
        keyboardActions: KeyboardActions = KeyboardActions(),
        backgroundColor: Color = MaterialTheme.colorScheme.background
    ) {

        var hidePassword by remember {
            mutableStateOf(true)
        }
        Column(modifier = modifier) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { this.contentDescription = contentDescription },
                leadingIcon = if (leadingIcon == null) null else {
                    {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                enabled = isEnabled,
                isError = errorMessage != null,
                trailingIcon = if (!isPassword) null else {
                    {
                        TextButton(
                            onClick = { hidePassword = !hidePassword },
                            enabled = isEnabled
                        ) {
                            Icon(
                                painter = painterResource(if (hidePassword) R.drawable.ic_eye_close else R.drawable.ic_eye_open),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                shape = CutCornerShape(SHAPE_SMALL),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = backgroundColor,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.surface,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.surface,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.5f
                    ),
                    errorBorderColor = MaterialTheme.colorScheme.error
                ),
                visualTransformation = if (isPassword && hidePassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions
            )


            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    @Composable
    fun Password(
        modifier: Modifier = Modifier,
        value: String,
        onValueChange: (String) -> Unit,
        contentDescription: String,
        label: String = "",
        placeholder: String = "",
        errorMessage: String? = null,
        leadingIcon: ImageVector? = Icons.Outlined.Lock,
        isEnabled: Boolean = true,
        keyboardOptions: KeyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions: KeyboardActions = KeyboardActions(),
        backgroundColor: Color = MaterialTheme.colorScheme.background
    ) {
        CustomTextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            errorMessage = errorMessage,
            leadingIcon = leadingIcon,
            isPassword = true,
            isEnabled = isEnabled,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            backgroundColor = backgroundColor,
            contentDescription = contentDescription
        )
    }

    @Composable
    fun Email(
        modifier: Modifier = Modifier,
        value: String,
        onValueChange: (String) -> Unit,
        contentDescription: String,
        label: String = "",
        placeholder: String = "",
        errorMessage: String? = null,
        leadingIcon: ImageVector? = Icons.Outlined.Email,
        isEnabled: Boolean = true,
        keyboardOptions: KeyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions: KeyboardActions = KeyboardActions(),
        backgroundColor: Color = MaterialTheme.colorScheme.background
    ) {
        CustomTextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            errorMessage = errorMessage,
            leadingIcon = leadingIcon,
            isPassword = false,
            isEnabled = isEnabled,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            backgroundColor = backgroundColor,
            contentDescription = contentDescription
        )
    }
}

@Preview
@Composable
fun TextFieldPreview() {
    CustomTextField.Email(
        value = "",
        onValueChange = {},
        leadingIcon = Icons.Outlined.MailOutline,
        label = "Email",
        placeholder = "Email",
        contentDescription = ""
    )
}

@Preview
@Composable
fun TextFieldErrorPreview() {
    CustomTextField.Password(
        value = "",
        onValueChange = {},
        leadingIcon = Icons.Outlined.Lock,
        label = "Password",
        placeholder = "Password",
        errorMessage = "Invalid Password",
        contentDescription = ""
    )
}