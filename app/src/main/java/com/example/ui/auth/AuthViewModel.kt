package com.example.ui.auth

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.*
import com.example.data.db.DeskPetDatabase
import com.example.data.repository.DeskPetRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

enum class AuthMode {
    LOGIN,
    SIGN_UP
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authPreferences = AuthPreferences(application)
    private val authClient = SupabaseAuthClient(authPreferences)
    private val deskPetRepository: DeskPetRepository

    init {
        val database = DeskPetDatabase.getDatabase(application)
        deskPetRepository = DeskPetRepository(database.dao())
    }

    // Auth screen mode (Login vs Create Account)
    private val _authMode = MutableStateFlow(AuthMode.LOGIN)
    val authMode: StateFlow<AuthMode> = _authMode.asStateFlow()

    fun setAuthMode(mode: AuthMode) {
        _authMode.value = mode
        _errorMessage.value = null
        _successMessage.value = null
    }

    // Input fields
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    fun onEmailChanged(value: String) { _email.value = value }
    fun onPasswordChanged(value: String) { _password.value = value }
    fun onConfirmPasswordChanged(value: String) { _confirmPassword.value = value }
    fun onDisplayNameChanged(value: String) { _displayName.value = value }
    fun onUsernameChanged(value: String) { _username.value = value }
    fun togglePasswordVisibility() { _isPasswordVisible.value = !_isPasswordVisible.value }

    // Loading & Feedback
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    // Authentication session state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser: MutableStateFlow<SupabaseUser?> = MutableStateFlow(
        authPreferences.getCurrentUser() ?: SupabaseUser(
            id = "user_default",
            email = "alex.rivers@gmail.com",
            displayName = "Desk Companion",
            username = "desk_friend",
            avatarUrl = ""
        )
    )
    val currentUser: StateFlow<SupabaseUser?> = _currentUser.asStateFlow()

    private val _authProvider = MutableStateFlow(
        if (authPreferences.authProvider != AuthProvider.GUEST) authPreferences.authProvider else AuthProvider.GOOGLE
    )
    val authProvider: StateFlow<AuthProvider> = _authProvider.asStateFlow()

    // Supabase Configuration
    private val _supabaseConfig = MutableStateFlow(authPreferences.getEffectiveSupabaseConfig())
    val supabaseConfig: StateFlow<SupabaseConfig> = _supabaseConfig.asStateFlow()

    private val _showConfigDialog = MutableStateFlow(false)
    val showConfigDialog: StateFlow<Boolean> = _showConfigDialog.asStateFlow()

    fun setConfigDialogVisible(visible: Boolean) {
        _showConfigDialog.value = visible
    }

    // Unconfirmed email notice & Instant Access support
    private val _unconfirmedEmail = MutableStateFlow<String?>(null)
    val unconfirmedEmail: StateFlow<String?> = _unconfirmedEmail.asStateFlow()

    private val _pendingUser = MutableStateFlow<SupabaseUser?>(null)
    val pendingUser: StateFlow<SupabaseUser?> = _pendingUser.asStateFlow()

    private val _isResending = MutableStateFlow(false)
    val isResending: StateFlow<Boolean> = _isResending.asStateFlow()

    private val _resendStatus = MutableStateFlow<String?>(null)
    val resendStatus: StateFlow<String?> = _resendStatus.asStateFlow()

    private val _showEmailHelpDialog = MutableStateFlow(false)
    val showEmailHelpDialog: StateFlow<Boolean> = _showEmailHelpDialog.asStateFlow()

    fun setShowEmailHelpDialog(visible: Boolean) {
        _showEmailHelpDialog.value = visible
    }

    fun dismissUnconfirmedEmailBanner() {
        _unconfirmedEmail.value = null
        _pendingUser.value = null
        _resendStatus.value = null
    }

    fun resendConfirmationEmail() {
        val em = _unconfirmedEmail.value ?: _email.value.trim()
        if (em.isBlank()) {
            _errorMessage.value = "No email address found to resend confirmation."
            return
        }

        viewModelScope.launch {
            _isResending.value = true
            _resendStatus.value = null
            val result = authClient.resendConfirmationEmail(em)
            _isResending.value = false
            result.onSuccess { msg ->
                _resendStatus.value = msg
                _successMessage.value = msg
            }.onFailure { err ->
                _errorMessage.value = "Could not resend email: ${err.message}"
            }
        }
    }

    // Instant Access allows users to sign in immediately without waiting for confirmation emails
    fun enterWithInstantAccess() {
        val emailVal = _unconfirmedEmail.value ?: _email.value.trim()
        val currentPending = _pendingUser.value
        val pending = currentPending ?: SupabaseUser(
            id = "supabase_user_" + emailVal.hashCode().toString(),
            email = emailVal,
            displayName = _displayName.value.ifBlank { emailVal.substringBefore("@") },
            username = _username.value.ifBlank { emailVal.substringBefore("@").replace(".", "_") }
        )
        val session = SupabaseSession(
            accessToken = "instant_access_${System.currentTimeMillis()}",
            refreshToken = "instant_refresh_${System.currentTimeMillis()}",
            user = pending
        )
        authPreferences.saveSession(session, pending, AuthProvider.SUPABASE_EMAIL)
        viewModelScope.launch {
            onAuthSuccess(pending, AuthProvider.SUPABASE_EMAIL, "Welcome to Desk Pet, ${pending.displayName.ifBlank { pending.username }}!")
            _unconfirmedEmail.value = null
            _pendingUser.value = null
            _errorMessage.value = null
            _successMessage.value = null
        }
    }

    fun saveCustomConfig(url: String, anonKey: String) {
        authPreferences.customSupabaseUrl = url
        authPreferences.customSupabaseAnonKey = anonKey
        _supabaseConfig.value = authPreferences.getEffectiveSupabaseConfig()
        _successMessage.value = "Supabase configuration updated!"
        _showConfigDialog.value = false
    }

    // Perform Email & Password Login
    fun login() {
        val em = _email.value.trim()
        val pwd = _password.value

        if (em.isBlank()) {
            _errorMessage.value = "Please enter your email address."
            return
        }
        if (pwd.isBlank()) {
            _errorMessage.value = "Please enter your password."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val result = authClient.signInWithEmail(em, pwd)
            _isLoading.value = false

            when (result) {
                is AuthResult.Success -> {
                    _unconfirmedEmail.value = null
                    _pendingUser.value = null
                    onAuthSuccess(result.user, result.provider, result.message)
                }
                is AuthResult.NeedsEmailConfirmation -> {
                    _unconfirmedEmail.value = result.email
                    _pendingUser.value = result.user
                    _errorMessage.value = null
                    _successMessage.value = result.message
                }
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    // Perform Create Account / Sign Up
    fun signUp() {
        val em = _email.value.trim()
        val pwd = _password.value
        val confirmPwd = _confirmPassword.value
        val name = _displayName.value.trim()
        val uname = _username.value.trim()

        if (em.isBlank()) {
            _errorMessage.value = "Please enter your email address."
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
            _errorMessage.value = "Please enter a valid email address."
            return
        }
        if (pwd.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters."
            return
        }
        if (pwd != confirmPwd) {
            _errorMessage.value = "Passwords do not match."
            return
        }
        val finalName = if (name.isNotBlank()) name else em.substringBefore("@")
        val finalUname = if (uname.isNotBlank()) uname else em.substringBefore("@").replace(".", "_")

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val result = authClient.signUp(em, pwd, finalName, finalUname)
            _isLoading.value = false

            when (result) {
                is AuthResult.Success -> {
                    _unconfirmedEmail.value = null
                    _pendingUser.value = null
                    onAuthSuccess(result.user, result.provider, result.message)
                }
                is AuthResult.NeedsEmailConfirmation -> {
                    _unconfirmedEmail.value = result.email
                    _pendingUser.value = result.user
                    _successMessage.value = result.message
                    _authMode.value = AuthMode.LOGIN
                }
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    // Google Sign-In with Credential Manager
    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val config = _supabaseConfig.value
            val webClientId = config.googleWebClientId

            if (webClientId.isBlank() || webClientId.contains("placeholder") || webClientId.contains("your-google")) {
                // If Web Client ID is not configured yet, notify user and offer seamless demo Google option
                _isLoading.value = false
                _errorMessage.value = "Google Web Client ID is not configured in .env yet. You can sign in using 'Demo Google Account' or email."
                return@launch
            }

            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential

                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val email = googleIdTokenCredential.id
                    val displayName = googleIdTokenCredential.displayName ?: email.substringBefore("@")

                    val authResult = authClient.signInWithGoogleIdToken(
                        idToken = idToken,
                        emailFallback = email,
                        displayNameFallback = displayName
                    )
                    _isLoading.value = false
                    when (authResult) {
                        is AuthResult.Success -> {
                            onAuthSuccess(authResult.user, authResult.provider, authResult.message)
                        }
                        is AuthResult.Error -> {
                            _errorMessage.value = authResult.message
                        }
                        is AuthResult.NeedsEmailConfirmation -> {
                            _errorMessage.value = authResult.message
                        }
                    }
                } else {
                    _isLoading.value = false
                    _errorMessage.value = "Unexpected credential format received."
                }
            } catch (e: GetCredentialCancellationException) {
                _isLoading.value = false
                // User dismissed dialog
                Log.d("AuthViewModel", "Google sign-in cancelled by user")
            } catch (e: GetCredentialException) {
                _isLoading.value = false
                Log.w("AuthViewModel", "Credential manager exception: ${e.message}")
                _errorMessage.value = "Google Sign-In: ${e.localizedMessage ?: "Failed to retrieve Google credentials"}. Try Demo Google Sign-In below!"
            } catch (e: Exception) {
                _isLoading.value = false
                Log.e("AuthViewModel", "Google sign-in error", e)
                _errorMessage.value = "Google Sign-In error: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    // Instant Google Demo login for immediate prototyping & preview without cloud console setup
    fun signInWithGoogleDemo() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val demoUser = SupabaseUser(
                id = "google_demo_101",
                email = "demo.companion@gmail.com",
                displayName = "Alex Rivers",
                username = "alex_study",
                avatarUrl = ""
            )
            authPreferences.saveSession(null, demoUser, AuthProvider.GOOGLE)
            _isLoading.value = false
            onAuthSuccess(demoUser, AuthProvider.GOOGLE, "Signed in with Google Account (Demo)!")
        }
    }

    // Instant Supabase Demo account login
    fun signInWithSupabaseDemo() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val demoUser = SupabaseUser(
                id = "supabase_demo_202",
                email = "mochi.friend@deskpet.app",
                displayName = "Bilawal",
                username = "kbilawal",
                avatarUrl = ""
            )
            val demoSession = SupabaseSession(
                accessToken = "demo_token_${System.currentTimeMillis()}",
                refreshToken = "demo_refresh_${System.currentTimeMillis()}",
                user = demoUser
            )
            authPreferences.saveSession(demoSession, demoUser, AuthProvider.SUPABASE_EMAIL)
            _isLoading.value = false
            onAuthSuccess(demoUser, AuthProvider.SUPABASE_EMAIL, "Logged in to Supabase Demo Account!")
        }
    }

    // Guest / Offline Mode
    fun continueAsGuest() {
        val guestUser = SupabaseUser(
            id = "guest_companion",
            email = "guest@deskpet.local",
            displayName = "Desk Companion",
            username = "guest_pet"
        )
        authPreferences.saveSession(null, guestUser, AuthProvider.GUEST)
        viewModelScope.launch {
            onAuthSuccess(guestUser, AuthProvider.GUEST, "Welcome! Exploring as guest.")
        }
    }

    // Sign out
    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            authClient.signOut()
            authPreferences.clearSession()
            _isLoggedIn.value = false
            _currentUser.value = null
            _authProvider.value = AuthProvider.GUEST
            _isLoading.value = false
            _successMessage.value = "Signed out successfully."
        }
    }

    private suspend fun onAuthSuccess(user: SupabaseUser, provider: AuthProvider, message: String) {
        _isLoggedIn.value = true
        _currentUser.value = user
        _authProvider.value = provider
        _successMessage.value = message

        // Seed and sync the specific user's database
        try {
            val userDb = DeskPetDatabase.getDatabaseForUser(
                getApplication(),
                userId = user.id,
                displayName = user.displayName,
                username = user.username
            )
            if (userDb.dao().getActivePet().firstOrNull() == null) {
                DeskPetDatabase.populateInitialData(
                    dao = userDb.dao(),
                    userId = user.id,
                    username = user.username,
                    displayName = user.displayName,
                    avatarUrl = user.avatarUrl
                )
            } else {
                DeskPetRepository(userDb.dao()).updateUserProfileFromAuth(
                    displayName = user.displayName,
                    username = user.username,
                    avatarUrl = user.avatarUrl
                )
            }
        } catch (e: Exception) {
            Log.w("AuthViewModel", "Error initializing user database: ${e.message}")
        }
    }
}
