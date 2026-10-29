package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.auth.AuthMode
import com.example.ui.auth.AuthViewModel
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val authMode by authViewModel.authMode.collectAsStateWithLifecycle()
    val email by authViewModel.email.collectAsStateWithLifecycle()
    val password by authViewModel.password.collectAsStateWithLifecycle()
    val confirmPassword by authViewModel.confirmPassword.collectAsStateWithLifecycle()
    val displayName by authViewModel.displayName.collectAsStateWithLifecycle()
    val username by authViewModel.username.collectAsStateWithLifecycle()
    val isPasswordVisible by authViewModel.isPasswordVisible.collectAsStateWithLifecycle()
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by authViewModel.successMessage.collectAsStateWithLifecycle()
    val supabaseConfig by authViewModel.supabaseConfig.collectAsStateWithLifecycle()
    val showConfigDialog by authViewModel.showConfigDialog.collectAsStateWithLifecycle()
    val unconfirmedEmail by authViewModel.unconfirmedEmail.collectAsStateWithLifecycle()
    val isResending by authViewModel.isResending.collectAsStateWithLifecycle()
    val resendStatus by authViewModel.resendStatus.collectAsStateWithLifecycle()
    val showEmailHelpDialog by authViewModel.showEmailHelpDialog.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.clearMessages()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // App Icon Logo Header (Replaces Mochi mascot)
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = CozyCardBackground,
                border = BorderStroke(2.dp, Primary.copy(alpha = 0.5f)),
                shadowElevation = 6.dp,
                modifier = Modifier.size(92.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_pet),
                        contentDescription = "Desk Pet Logo",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Desk Pet",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your cozy desk companion & flow tracker",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Mode Selector Pill (Log In / Create Account)
            Surface(
                shape = CircleShape,
                color = SurfaceContainerLow,
                border = BorderStroke(1.dp, CozyCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Log In Button
                    Surface(
                        shape = CircleShape,
                        color = if (authMode == AuthMode.LOGIN) Primary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { authViewModel.setAuthMode(AuthMode.LOGIN) }
                            .testTag("login_tab_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Log In",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (authMode == AuthMode.LOGIN) OnPrimary else OnSurfaceVariant,
                                fontWeight = if (authMode == AuthMode.LOGIN) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }

                    // Create Account Button
                    Surface(
                        shape = CircleShape,
                        color = if (authMode == AuthMode.SIGN_UP) Primary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { authViewModel.setAuthMode(AuthMode.SIGN_UP) }
                            .testTag("signup_tab_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Create Account",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (authMode == AuthMode.SIGN_UP) OnPrimary else OnSurfaceVariant,
                                fontWeight = if (authMode == AuthMode.SIGN_UP) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Unconfirmed Email Action Notice Card (Dismissible support)
            if (unconfirmedEmail != null) {
                EmailConfirmationNoticeCard(
                    email = unconfirmedEmail ?: "",
                    isResending = isResending,
                    resendStatus = resendStatus,
                    onInstantAccess = { authViewModel.enterWithInstantAccess() },
                    onResend = { authViewModel.resendConfirmationEmail() },
                    onShowHelp = { authViewModel.setShowEmailHelpDialog(true) },
                    onDismiss = { authViewModel.dismissUnconfirmedEmailBanner() }
                )
            }

            // Input Fields Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CozyCardBackground,
                border = BorderStroke(1.dp, CozyCardBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Sign-Up Specific Fields
                    if (authMode == AuthMode.SIGN_UP) {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { authViewModel.onDisplayNameChanged(it) },
                            label = { Text("Display Name") },
                            placeholder = { Text("e.g. Bilawal") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Person, contentDescription = null, tint = Primary)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("display_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = CozyCardBorder,
                                focusedContainerColor = SurfaceContainerLow,
                                unfocusedContainerColor = SurfaceContainerLow
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { authViewModel.onUsernameChanged(it) },
                            label = { Text("Username") },
                            placeholder = { Text("e.g. kbilawal") },
                            leadingIcon = {
                                Icon(Icons.Outlined.AlternateEmail, contentDescription = null, tint = Primary)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("username_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = CozyCardBorder,
                                focusedContainerColor = SurfaceContainerLow,
                                unfocusedContainerColor = SurfaceContainerLow
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        )
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { authViewModel.onEmailChanged(it) },
                        label = { Text("Email Address") },
                        placeholder = { Text("name@example.com") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = Primary)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = CozyCardBorder,
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { authViewModel.onPasswordChanged(it) },
                        label = { Text("Password") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = Primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { authViewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = CozyCardBorder,
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (authMode == AuthMode.SIGN_UP) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            onDone = {
                                focusManager.clearFocus()
                                authViewModel.login()
                            }
                        )
                    )

                    // Confirm Password (Sign-up only)
                    if (authMode == AuthMode.SIGN_UP) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { authViewModel.onConfirmPasswordChanged(it) },
                            label = { Text("Confirm Password") },
                            placeholder = { Text("••••••••") },
                            leadingIcon = {
                                Icon(Icons.Outlined.LockReset, contentDescription = null, tint = Primary)
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = CozyCardBorder,
                                focusedContainerColor = SurfaceContainerLow,
                                unfocusedContainerColor = SurfaceContainerLow
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    authViewModel.signUp()
                                }
                            )
                        )
                    }

                    // Main Action Button (Log In or Create Account)
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (authMode == AuthMode.LOGIN) {
                                authViewModel.login()
                            } else {
                                authViewModel.signUp()
                            }
                        },
                        enabled = !isLoading,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_auth_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = OnPrimary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (authMode == AuthMode.LOGIN) Icons.AutoMirrored.Filled.Login else Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (authMode == AuthMode.LOGIN) "Log In" else "Create Account",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Helper for Sign-Up email delivery
                    if (authMode == AuthMode.SIGN_UP) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { authViewModel.setShowEmailHelpDialog(true) }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Didn't get an email? Tap for Instant Access & help",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Divider: Or Continue With
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = CozyCardBorder)
                Text(
                    text = "or continue with",
                    style = MaterialTheme.typography.bodySmall,
                    color = Outline
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = CozyCardBorder)
            }

            // Google Sign-In Button (Fully visible in both Dark & Light modes)
            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
                    authViewModel.signInWithGoogle(context)
                },
                enabled = !isLoading,
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isDark) SurfaceContainerLow else Color.White,
                    contentColor = if (isDark) Color.White else Color(0xFF1F1F1F)
                ),
                border = BorderStroke(1.5.dp, if (isDark) CozyCardBorder else PrimaryFixedDim),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("google_signin_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFF1F3F4),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF4285F4),
                                fontSize = 17.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF1F1F1F)
                    )
                }
            }

            // Continue as Guest Option
            TextButton(
                onClick = { authViewModel.continueAsGuest() },
                modifier = Modifier
                    .testTag("guest_signin_button")
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = "Continue as Guest (Explore First) 🐾",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Floating Snackbar Host for sign in / out notifications
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                shape = RoundedCornerShape(16.dp),
                containerColor = CozyCardBackgroundElevated,
                contentColor = MaterialTheme.colorScheme.onSurface,
                actionColor = Primary,
                dismissActionContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }

    // Supabase Configuration Dialog
    if (showConfigDialog) {
        SupabaseConfigDialog(
            currentConfig = supabaseConfig,
            onDismiss = { authViewModel.setConfigDialogVisible(false) },
            onSave = { url, key ->
                authViewModel.saveCustomConfig(url, key)
            }
        )
    }

    // Why No Email / Supabase Settings Help Dialog
    if (showEmailHelpDialog) {
        EmailConfirmationHelpDialog(
            unconfirmedEmail = unconfirmedEmail,
            onInstantAccess = { authViewModel.enterWithInstantAccess() },
            onDismiss = { authViewModel.setShowEmailHelpDialog(false) }
        )
    }
}

@Composable
fun EmailConfirmationNoticeCard(
    email: String,
    isResending: Boolean,
    resendStatus: String?,
    onInstantAccess: () -> Unit,
    onResend: () -> Unit,
    onShowHelp: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFFFF9E6),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFCA28)),
        shadowElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFECB3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Email Confirmation Notice",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4E342E)
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss notice",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = "Supabase free projects limit confirmation emails to 3-4/hour, and they often land in Spam/Junk. You don't have to wait—click Instant Access to enter right now!",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4E342E)
            )

            if (resendStatus != null) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = resendStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Instant Access Button (Primary Action)
                Button(
                    onClick = onInstantAccess,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("instant_access_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Instant Access 🚀",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Resend Button
                OutlinedButton(
                    onClick = onResend,
                    enabled = !isResending,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("resend_email_button")
                ) {
                    if (isResending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Primary
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Primary
                            )
                            Text(
                                text = "Resend",
                                style = MaterialTheme.typography.labelLarge,
                                color = Primary
                            )
                        }
                    }
                }
            }

            // Help link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowHelp() }
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Why didn't I get an email? (Troubleshoot & Fix)",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EmailConfirmationHelpDialog(
    unconfirmedEmail: String?,
    onInstantAccess: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = Primary
                )
                Text("Why No Email Arrived & Fix", style = MaterialTheme.typography.titleMedium)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section 1: Spam Folder
                Surface(
                    color = SurfaceContainerLow,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "1. Check Spam / Junk / Promotions",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Confirmation emails are sent from noreply@mail.app.supabase.io. Email providers often classify automated confirmation emails as Spam or Promotions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Section 2: Supabase Rate Limit
                Surface(
                    color = SurfaceContainerLow,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "2. Supabase Free Tier Rate Limit",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "By default, free Supabase projects are strictly limited to sending 3 to 4 emails per hour total. If multiple accounts were created recently, Supabase pauses sending emails until the next hour.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Section 3: Instant Access
                Surface(
                    color = PrimaryFixed.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryFixedDim),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "3. Instant Access (Recommended)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You don't need to wait! Click 'Instant Access Now' below to immediately activate your account and start playing with Desk Pet right away.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Section 4: Supabase Dashboard Fix for Admin/Owner
                Surface(
                    color = SurfaceContainerLow,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "4. Permanent Fix in Supabase Dashboard",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "To allow all users to sign up and log in without requiring confirmation emails:\n" +
                                    "• Go to https://supabase.com/dashboard\n" +
                                    "• Select your project -> Authentication -> Providers\n" +
                                    "• Click 'Email' and toggle OFF 'Confirm email'\n" +
                                    "• Click 'Save'\n\n" +
                                    "Or configure a custom SMTP provider (Resend, SendGrid) under Project Settings -> Auth -> SMTP.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (!unconfirmedEmail.isNullOrBlank()) {
                Button(
                    onClick = {
                        onDismiss()
                        onInstantAccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("dialog_instant_access_button")
                ) {
                    Text("Instant Access Now 🚀")
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Got It")
                }
            }
        },
        dismissButton = {
            if (!unconfirmedEmail.isNullOrBlank()) {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}

@Composable
fun SupabaseConfigDialog(
    currentConfig: com.example.data.auth.SupabaseConfig,
    onDismiss: () -> Unit,
    onSave: (url: String, key: String) -> Unit
) {
    var urlText by remember { mutableStateOf(currentConfig.projectUrl) }
    var keyText by remember { mutableStateOf(currentConfig.anonKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("⚡ Supabase Settings")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "You can set SUPABASE_URL and SUPABASE_ANON_KEY in AI Studio Secrets, or enter your project details here for immediate live connection:",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )

                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    label = { Text("Supabase Project URL") },
                    placeholder = { Text("https://your-ref.supabase.co") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("supabase_url_input")
                )

                OutlinedTextField(
                    value = keyText,
                    onValueChange = { keyText = it },
                    label = { Text("Supabase Anon Public Key") },
                    placeholder = { Text("eyJhbGciOi...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("supabase_anon_key_input")
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryFixed.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Text(
                            text = "Tip for email verification: In your Supabase Dashboard under Authentication -> Providers -> Email, turn off 'Confirm email' so users can sign in immediately without needing external SMTP setup.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(urlText.trim(), keyText.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.testTag("save_supabase_config_button")
            ) {
                Text("Save & Connect")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
