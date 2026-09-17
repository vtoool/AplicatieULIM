package com.victor.ulim.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.victor.ulim.R
import com.victor.ulim.ui.AppViewModel
import com.victor.ulim.ui.components.AnimatedGradientBackground
import com.victor.ulim.ui.components.LanguageMenu
import com.victor.ulim.ui.components.LogoBadge
import com.victor.ulim.ui.components.Stagger
import com.victor.ulim.ui.components.rememberShakeState
import com.victor.ulim.ui.components.shake
import kotlinx.coroutines.launch

/**
 * "1 pagina de logare" — verifies the password (against the seeded demo account),
 * animates, and opens the form on success.
 */
@Composable
fun LoginScreen(
    vm: AppViewModel,
    snackbarHostState: SnackbarHostState,
    onLoginSuccess: () -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var loginFailed by rememberSaveable { mutableStateOf(false) }
    var checking by remember { mutableStateOf(false) }
    val shake = rememberShakeState()
    val scope = rememberCoroutineScope()
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedGradientBackground()

        LanguageMenu(
            current = vm.language,
            onSelect = vm::selectLanguage,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp)
                .zIndex(1f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Stagger(0) { LogoBadge() }
            Spacer(Modifier.height(20.dp))
            Stagger(1) {
                Text(
                    text = stringResource(R.string.login_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = cs.onBackground
                )
            }
            Spacer(Modifier.height(6.dp))
            Stagger(2) {
                Text(
                    text = stringResource(R.string.login_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onBackground.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(28.dp))
            Stagger(3) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 6.dp,
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp)
                        .shake(shake)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it; loginFailed = false },
                            label = { Text(stringResource(R.string.username)) },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            isError = loginFailed,
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; loginFailed = false },
                            label = { Text(stringResource(R.string.password)) },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            isError = loginFailed,
                            supportingText = if (loginFailed) {
                                { Text(stringResource(R.string.login_error), color = cs.error) }
                            } else null,
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (checking) return@Button
                                checking = true
                                scope.launch {
                                    val ok = vm.login(username, password)
                                    checking = false
                                    if (ok) {
                                        launch {
                                            snackbarHostState.showSnackbar(
                                                context.getString(R.string.login_success)
                                            )
                                        }
                                        onLoginSuccess()
                                    } else {
                                        loginFailed = true
                                        shake.shake()
                                    }
                                }
                            },
                            enabled = !checking,
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            if (checking) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                    color = cs.onPrimary
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.login_button),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        if (vm.loggedIn) {
                            TextButton(
                                onClick = { vm.logout() },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.size(6.dp))
                                Text(stringResource(R.string.logout_label))
                            }
                        }
                        Text(
                            text = stringResource(R.string.demo_credentials),
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
