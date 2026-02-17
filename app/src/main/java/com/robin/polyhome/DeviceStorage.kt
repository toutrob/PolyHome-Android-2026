package com.robin.polyhome

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "device_settings")

class DeviceStorage(private val context: Context) {

    suspend fun saveName(houseId: String, deviceId: String, name: String) {
        val key = stringPreferencesKey("${houseId}_${deviceId}")

        context.dataStore.edit { preferences ->
            preferences[key] = name
        }
    }

    suspend fun getName(houseId: String?, deviceId: String): String? {
        val key = stringPreferencesKey("${houseId}_${deviceId}")
        val preferences = context.dataStore.data.first()
        return preferences[key]
    }
}