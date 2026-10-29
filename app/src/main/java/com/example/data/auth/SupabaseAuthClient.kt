package com.example.data.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseAuthClient(private val authPreferences: AuthPreferences) {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun signUp(
        email: String,
        pass: String,
        displayName: String,
        username: String
    ): AuthResult = withContext(Dispatchers.IO) {
        val config = authPreferences.getEffectiveSupabaseConfig()
        if (!config.isConfigured) {
            return@withContext AuthResult.Error(
                "Supabase is not configured yet. Please configure your SUPABASE_URL and SUPABASE_ANON_KEY in the Secrets panel or in the Supabase Settings below."
            )
        }

        try {
            val url = "${config.projectUrl}/auth/v1/signup"
            val metadata = JSONObject().apply {
                put("full_name", displayName)
                put("username", username)
            }
            val bodyJson = JSONObject().apply {
                put("email", email.trim())
                put("password", pass)
                put("data", metadata)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", config.anonKey)
                .addHeader("Authorization", "Bearer ${config.anonKey}")
                .addHeader("Content-Type", "application/json")
                .post(bodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = parseErrorMessage(responseBody, response.code)
                return@withContext AuthResult.Error(errorMsg)
            }

            val json = JSONObject(responseBody)
            val accessToken = json.optString("access_token", "")
            val refreshToken = json.optString("refresh_token", "")
            val userObj = json.optJSONObject("user") ?: json

            val parsedUser = parseUser(userObj, fallbackEmail = email, fallbackName = displayName, fallbackUsername = username)

            if (accessToken.isNotBlank()) {
                val session = SupabaseSession(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    tokenType = json.optString("token_type", "bearer"),
                    expiresIn = json.optLong("expires_in", 3600),
                    user = parsedUser
                )
                authPreferences.saveSession(session, parsedUser, AuthProvider.SUPABASE_EMAIL)
                AuthResult.Success(
                    session = session,
                    user = parsedUser,
                    provider = AuthProvider.SUPABASE_EMAIL,
                    message = "Welcome to Desk Pet, ${parsedUser.displayName.ifBlank { parsedUser.username }}!"
                )
            } else {
                // Supabase email confirmation might be enabled
                AuthResult.NeedsEmailConfirmation(
                    email = email,
                    message = "Account created! A confirmation email was requested for $email. If not received, please check your spam folder or use Instant Access below.",
                    user = parsedUser
                )
            }
        } catch (e: Exception) {
            AuthResult.Error("Network error during sign-up: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): AuthResult = withContext(Dispatchers.IO) {
        val config = authPreferences.getEffectiveSupabaseConfig()
        if (!config.isConfigured) {
            return@withContext AuthResult.Error(
                "Supabase is not configured yet. Please configure your SUPABASE_URL and SUPABASE_ANON_KEY in the Secrets panel or in the Supabase Settings below."
            )
        }

        try {
            val url = "${config.projectUrl}/auth/v1/token?grant_type=password"
            val bodyJson = JSONObject().apply {
                put("email", email.trim())
                put("password", pass)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", config.anonKey)
                .addHeader("Authorization", "Bearer ${config.anonKey}")
                .addHeader("Content-Type", "application/json")
                .post(bodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = parseErrorMessage(responseBody, response.code)
                if (errorMsg.contains("Email not confirmed", ignoreCase = true)) {
                    val fallbackUser = SupabaseUser(
                        id = "supabase_user_" + email.hashCode().toString(),
                        email = email,
                        displayName = email.substringBefore("@"),
                        username = email.substringBefore("@").replace(".", "_")
                    )
                    return@withContext AuthResult.NeedsEmailConfirmation(
                        email = email,
                        message = "Your email ($email) hasn't been confirmed yet. Free Supabase plans often limit emails or filter to spam. You can resend or enter immediately below!",
                        user = fallbackUser
                    )
                }
                return@withContext AuthResult.Error(errorMsg)
            }

            val json = JSONObject(responseBody)
            val accessToken = json.optString("access_token", "")
            val refreshToken = json.optString("refresh_token", "")
            val userObj = json.optJSONObject("user") ?: json

            val parsedUser = parseUser(userObj, fallbackEmail = email)

            val session = SupabaseSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                tokenType = json.optString("token_type", "bearer"),
                expiresIn = json.optLong("expires_in", 3600),
                user = parsedUser
            )
            authPreferences.saveSession(session, parsedUser, AuthProvider.SUPABASE_EMAIL)

            AuthResult.Success(
                session = session,
                user = parsedUser,
                provider = AuthProvider.SUPABASE_EMAIL,
                message = "Welcome back, ${parsedUser.displayName.ifBlank { parsedUser.username.ifBlank { "Companion" } }}!"
            )
        } catch (e: Exception) {
            AuthResult.Error("Network error during sign-in: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun signInWithGoogleIdToken(
        idToken: String,
        emailFallback: String = "",
        displayNameFallback: String = ""
    ): AuthResult = withContext(Dispatchers.IO) {
        val config = authPreferences.getEffectiveSupabaseConfig()

        // If Supabase backend is configured, exchange with Supabase GoTrue
        if (config.isConfigured) {
            try {
                val url = "${config.projectUrl}/auth/v1/token?grant_type=id_token"
                val bodyJson = JSONObject().apply {
                    put("provider", "google")
                    put("id_token", idToken)
                }

                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", config.anonKey)
                    .addHeader("Authorization", "Bearer ${config.anonKey}")
                    .addHeader("Content-Type", "application/json")
                    .post(bodyJson.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val json = JSONObject(responseBody)
                    val accessToken = json.optString("access_token", "")
                    val refreshToken = json.optString("refresh_token", "")
                    val userObj = json.optJSONObject("user") ?: json
                    val parsedUser = parseUser(userObj, fallbackEmail = emailFallback, fallbackName = displayNameFallback)

                    val session = SupabaseSession(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        user = parsedUser
                    )
                    authPreferences.saveSession(session, parsedUser, AuthProvider.GOOGLE)

                    return@withContext AuthResult.Success(
                        session = session,
                        user = parsedUser,
                        provider = AuthProvider.GOOGLE,
                        message = "Logged in with Google via Supabase!"
                    )
                }
            } catch (e: Exception) {
                // If Supabase Google OAuth provider is not yet enabled on the dashboard, fall through to direct Google credentials
            }
        }

        // Direct Google Sign-In completion
        val user = SupabaseUser(
            id = "google_" + (if (emailFallback.isNotBlank()) emailFallback.hashCode().toString() else System.currentTimeMillis().toString()),
            email = emailFallback.ifBlank { "google_user@gmail.com" },
            displayName = displayNameFallback.ifBlank { "Google Explorer" },
            username = emailFallback.substringBefore("@").ifBlank { "google_user" }
        )
        authPreferences.saveSession(null, user, AuthProvider.GOOGLE)
        AuthResult.Success(
            session = null,
            user = user,
            provider = AuthProvider.GOOGLE,
            message = "Signed in with Google! Welcome ${user.displayName}."
        )
    }

    suspend fun resetPassword(email: String): Result<String> = withContext(Dispatchers.IO) {
        val config = authPreferences.getEffectiveSupabaseConfig()
        if (!config.isConfigured) {
            return@withContext Result.failure(Exception("Supabase is not configured yet."))
        }

        try {
            val url = "${config.projectUrl}/auth/v1/recover"
            val bodyJson = JSONObject().apply {
                put("email", email.trim())
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", config.anonKey)
                .addHeader("Authorization", "Bearer ${config.anonKey}")
                .addHeader("Content-Type", "application/json")
                .post(bodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success("Password recovery link sent to $email!")
            } else {
                val errorMsg = parseErrorMessage(response.body?.string() ?: "", response.code)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendConfirmationEmail(email: String): Result<String> = withContext(Dispatchers.IO) {
        val config = authPreferences.getEffectiveSupabaseConfig()
        if (!config.isConfigured) {
            return@withContext Result.failure(Exception("Supabase is not configured yet."))
        }

        try {
            val url = "${config.projectUrl}/auth/v1/resend"
            val bodyJson = JSONObject().apply {
                put("type", "signup")
                put("email", email.trim())
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", config.anonKey)
                .addHeader("Authorization", "Bearer ${config.anonKey}")
                .addHeader("Content-Type", "application/json")
                .post(bodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                Result.success("Confirmation email resent to $email! Please check your inbox and spam folder.")
            } else {
                val errorMsg = parseErrorMessage(responseBody, response.code)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error while resending confirmation: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }

    suspend fun signOut(): Boolean = withContext(Dispatchers.IO) {
        val config = authPreferences.getEffectiveSupabaseConfig()
        val token = authPreferences.accessToken

        if (config.isConfigured && token.isNotBlank()) {
            try {
                val url = "${config.projectUrl}/auth/v1/logout"
                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", config.anonKey)
                    .addHeader("Authorization", "Bearer $token")
                    .post("{}".toRequestBody(jsonMediaType))
                    .build()
                okHttpClient.newCall(request).execute()
            } catch (e: Exception) {
                // Ignore network error on logout
            }
        }

        authPreferences.clearSession()
        true
    }

    private fun parseUser(
        userObj: JSONObject,
        fallbackEmail: String = "",
        fallbackName: String = "",
        fallbackUsername: String = ""
    ): SupabaseUser {
        val id = userObj.optString("id", System.currentTimeMillis().toString())
        val email = userObj.optString("email", fallbackEmail)
        val metadata = userObj.optJSONObject("user_metadata")

        val fullName = metadata?.optString("full_name", "")
            ?.ifBlank { metadata.optString("name", "") }
            ?: fallbackName

        val username = metadata?.optString("username", "")
            ?.ifBlank { email.substringBefore("@") }
            ?: fallbackUsername.ifBlank { email.substringBefore("@") }

        val avatar = metadata?.optString("avatar_url", "")
            ?: metadata?.optString("picture", "")
            ?: ""

        val createdAt = userObj.optString("created_at", "")

        return SupabaseUser(
            id = id,
            email = email,
            displayName = if (fullName.isNotBlank()) fullName else username,
            username = username,
            avatarUrl = avatar,
            createdAt = createdAt
        )
    }

    private fun parseErrorMessage(responseBody: String, statusCode: Int): String {
        return try {
            val json = JSONObject(responseBody)
            val desc = json.optString("error_description", "")
            val msg = json.optString("msg", "")
            val message = json.optString("message", "")
            val error = json.optString("error", "")

            when {
                desc.isNotBlank() -> desc
                msg.isNotBlank() -> msg
                message.isNotBlank() -> message
                error.isNotBlank() -> error
                statusCode == 400 -> "Invalid credentials or request data."
                statusCode == 401 -> "Unauthorized. Check your email, password, or Supabase anon key."
                statusCode == 422 -> "Unprocessable entity. Please verify your email format and password length."
                else -> "Supabase request failed with status code $statusCode."
            }
        } catch (e: Exception) {
            if (responseBody.isNotBlank()) responseBody else "Request failed ($statusCode)"
        }
    }
}
