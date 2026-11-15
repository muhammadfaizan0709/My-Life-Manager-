package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class SettingsManager(private val context: Context) {
    companion object {
        val WATER_GOAL = intPreferencesKey("water_goal")
        val HEIGHT_CM = floatPreferencesKey("height_cm")
        val WEIGHT_KG = floatPreferencesKey("weight_kg")
        val THEME_MODE = intPreferencesKey("theme_mode") // 0 = System, 1 = Light, 2 = Dark
        val VAULT_PIN = stringPreferencesKey("vault_pin")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    val waterGoal: Flow<Int> = context.dataStore.data.map { it[WATER_GOAL] ?: 2000 }
    val heightCm: Flow<Float> = context.dataStore.data.map { it[HEIGHT_CM] ?: 170f }
    val themeMode: Flow<Int> = context.dataStore.data.map { it[THEME_MODE] ?: 0 }
    val vaultPin: Flow<String?> = context.dataStore.data.map { it[VAULT_PIN] }
    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME] ?: "User" }

    suspend fun setWaterGoal(goal: Int) {
        context.dataStore.edit { it[WATER_GOAL] = goal }
    }
    
    suspend fun setHeight(height: Float) {
        context.dataStore.edit { it[HEIGHT_CM] = height }
    }

    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { it[THEME_MODE] = mode }
    }

    suspend fun setVaultPin(pin: String?) {
        context.dataStore.edit { prefs ->
            if (pin == null) prefs.remove(VAULT_PIN) else prefs[VAULT_PIN] = pin
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[USER_NAME] = name }
    }
}
