package com.example.electrohub.base

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


object DataStoreManager {


    private const val PREF_DATASTORE_APP_NAME = "electrohub_datastore"



    const val PREF_KEY_ID = "id"
    const val PREF_KEY_USERNAME = "username"
    const val PREF_KEY_LOGGED_IN = "logged_in"

    // New
    const val PREF_KEY_LANGUAGE = "language"
    const val PREF_KEY_DARK_MODE = "dark_mode"



    val Context.dataStore by preferencesDataStore(
        name = PREF_DATASTORE_APP_NAME
    )







    suspend fun setUsername(
        context: Context,
        value: String,
        key: Preferences.Key<String>
    ){

        context.dataStore.edit { preferences ->

            preferences[key] = value

        }

    }






    suspend fun getUsername(
        context: Context,
        key: Preferences.Key<String>
    ): String {

        val preferences =
            context.dataStore.data.first()

        return preferences[key] ?: ""

    }

    suspend fun setLoggedIn(
        context: Context,
        value: Boolean,
        key: Preferences.Key<Boolean>
    ){

        context.dataStore.edit { preferences ->

            preferences[key] = value

        }

    }








    suspend fun setUserId(
        context: Context,
        value: String,
        key: Preferences.Key<String>
    ){

        context.dataStore.edit { preferences ->

            preferences[key] = value

        }

    }
    suspend fun getUserId(
        context: Context,
        key: Preferences.Key<String>
    ): String {


        val preferences =
            context.dataStore.data.first()


        return preferences[key] ?: ""

    }








    // =========================
    // Language
    // =========================


    suspend fun setLanguage(
        context: Context,
        value: String,
        key: Preferences.Key<String>
    ){

        context.dataStore.edit { preferences ->

            preferences[key] = value

        }

    }
    suspend fun getLanguage(
        context: Context,
        key: Preferences.Key<String>
    ): String {


        val preferences =
            context.dataStore.data.first()


        return preferences[key] ?: "en"

    }








    // =========================
    // Dark Mode
    // =========================


    suspend fun setDarkMode(
        context: Context,
        value: Boolean,
        key: Preferences.Key<Boolean>
    ){
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }

    }






    suspend fun getDarkMode(
        context: Context,
        key: Preferences.Key<Boolean>
    ): Boolean {

        val preferences = context.dataStore.data.first()

        return preferences[key] ?: false
    }



}