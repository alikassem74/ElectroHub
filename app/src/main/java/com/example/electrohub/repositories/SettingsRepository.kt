package com.example.electrohub.repositories

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.electrohub.base.DataStoreManager

class SettingsRepository {

    // =========================================================
    // LANGUAGE
    // =========================================================

    suspend fun saveLanguage(
        context: Context,
        language: String
    ) {

        DataStoreManager.setLanguage(
            context,
            language,
            stringPreferencesKey(
                DataStoreManager.PREF_KEY_LANGUAGE
            )
        )
    }


    suspend fun getLanguage(
        context: Context
    ): String {

        return DataStoreManager.getLanguage(
            context,
            stringPreferencesKey(
                DataStoreManager.PREF_KEY_LANGUAGE
            )
        )
    }


    // =========================================================
    // DARK MODE
    // =========================================================

    suspend fun saveDarkMode(
        context: Context,
        enabled: Boolean
    ) {

        DataStoreManager.setDarkMode(
            context,
            enabled,
            booleanPreferencesKey(
                DataStoreManager.PREF_KEY_DARK_MODE
            )
        )
    }


    suspend fun getDarkMode(
        context: Context
    ): Boolean {

        return DataStoreManager.getDarkMode(
            context,
            booleanPreferencesKey(
                DataStoreManager.PREF_KEY_DARK_MODE
            )
        )
    }


    // =========================================================
    // APPLY DARK MODE
    // =========================================================

    fun applyDarkMode(
        enabled: Boolean
    ) {

        AppCompatDelegate.setDefaultNightMode(
            if (enabled) {

                AppCompatDelegate.MODE_NIGHT_YES

            } else {

                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}