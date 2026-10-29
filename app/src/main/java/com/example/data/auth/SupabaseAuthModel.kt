package com.example.data.auth

data class SupabaseUser(
    val id: String,
    val email: String,
    val displayName: String = "",
    val username: String = "",
    val avatarUrl: String = "",
    val createdAt: String = ""
)

data class SupabaseSession(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "bearer",
    val expiresIn: Long = 3600,
    val user: SupabaseUser
)

enum class AuthProvider(val label: String) {
    SUPABASE_EMAIL("Supabase Email"),
    GOOGLE("Google Sign-In"),
    GUEST("Guest Companion")
}

sealed class AuthResult {
    data class Success(
        val session: SupabaseSession?,
        val user: SupabaseUser,
        val provider: AuthProvider,
        val message: String = ""
    ) : AuthResult()

    data class NeedsEmailConfirmation(
        val email: String,
        val message: String = "Registration successful! Please check your email to confirm your account before logging in.",
        val user: SupabaseUser? = null
    ) : AuthResult()

    data class Error(
        val message: String,
        val code: String? = null
    ) : AuthResult()
}

data class SupabaseConfig(
    val projectUrl: String,
    val anonKey: String,
    val googleWebClientId: String = ""
) {
    val isConfigured: Boolean
        get() = projectUrl.isNotBlank() &&
                !projectUrl.contains("your-project") &&
                !projectUrl.contains("xyzcompany") &&
                anonKey.isNotBlank() &&
                !anonKey.contains("placeholder")
}
