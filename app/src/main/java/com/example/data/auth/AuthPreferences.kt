package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

class AuthPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("desk_pet_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_USERNAME = "username"
        private const val KEY_AVATAR_URL = "avatar_url"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_PROVIDER = "auth_provider"
        private const val KEY_CUSTOM_SUPABASE_URL = "custom_supabase_url"
        private const val KEY_CUSTOM_SUPABASE_ANON_KEY = "custom_supabase_anon_key"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userId: String
        get() = prefs.getString(KEY_USER_ID, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var email: String
        get() = prefs.getString(KEY_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var displayName: String
        get() = prefs.getString(KEY_DISPLAY_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_DISPLAY_NAME, value).apply()

    var username: String
        get() = prefs.getString(KEY_USERNAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()

    var avatarUrl: String
        get() = prefs.getString(KEY_AVATAR_URL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_AVATAR_URL, value).apply()

    var accessToken: String
        get() = prefs.getString(KEY_ACCESS_TOKEN, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ACCESS_TOKEN, value).apply()

    var refreshToken: String
        get() = prefs.getString(KEY_REFRESH_TOKEN, "") ?: ""
        set(value) = prefs.edit().putString(KEY_REFRESH_TOKEN, value).apply()

    var authProvider: AuthProvider
        get() {
            val name = prefs.getString(KEY_PROVIDER, AuthProvider.GUEST.name) ?: AuthProvider.GUEST.name
            return try {
                AuthProvider.valueOf(name)
            } catch (e: Exception) {
                AuthProvider.GUEST
            }
        }
        set(value) = prefs.edit().putString(KEY_PROVIDER, value.name).apply()

    var customSupabaseUrl: String
        get() = prefs.getString(KEY_CUSTOM_SUPABASE_URL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_SUPABASE_URL, value.trim()).apply()

    var customSupabaseAnonKey: String
        get() = prefs.getString(KEY_CUSTOM_SUPABASE_ANON_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_SUPABASE_ANON_KEY, value.trim()).apply()

    fun getEffectiveSupabaseConfig(): SupabaseConfig {
        val buildConfigUrl = try {
            BuildConfig.SUPABASE_URL
        } catch (e: Throwable) {
            ""
        }

        val buildConfigKey = try {
            BuildConfig.SUPABASE_ANON_KEY
        } catch (e: Throwable) {
            ""
        }

        val buildConfigClientId = try {
            BuildConfig.GOOGLE_WEB_CLIENT_ID
        } catch (e: Throwable) {
            ""
        }

        val url = if (customSupabaseUrl.isNotBlank() && !customSupabaseUrl.contains("your-project") && !customSupabaseUrl.contains("xyzcompany")) {
            customSupabaseUrl
        } else {
            buildConfigUrl
        }
        val key = if (customSupabaseAnonKey.isNotBlank() && !customSupabaseAnonKey.contains("placeholder")) {
            customSupabaseAnonKey
        } else {
            buildConfigKey
        }

        return SupabaseConfig(
            projectUrl = url.trim().removeSuffix("/"),
            anonKey = key.trim(),
            googleWebClientId = buildConfigClientId.trim()
        )
    }

    fun saveSession(session: SupabaseSession?, user: SupabaseUser, provider: AuthProvider) {
        isLoggedIn = true
        userId = user.id
        email = user.email
        displayName = user.displayName
        username = user.username
        avatarUrl = user.avatarUrl
        authProvider = provider
        if (session != null) {
            accessToken = session.accessToken
            refreshToken = session.refreshToken
        }
    }

    fun clearSession() {
        isLoggedIn = false
        userId = ""
        email = ""
        displayName = ""
        username = ""
        avatarUrl = ""
        accessToken = ""
        refreshToken = ""
        authProvider = AuthProvider.GUEST
    }

    fun getCurrentUser(): SupabaseUser? {
        if (!isLoggedIn) return null
        return SupabaseUser(
            id = userId,
            email = email,
            displayName = displayName,
            username = username,
            avatarUrl = avatarUrl
        )
    }
}
