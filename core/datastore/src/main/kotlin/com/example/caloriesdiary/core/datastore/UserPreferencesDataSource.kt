package com.example.caloriesdiary.core.datastore

import androidx.datastore.core.DataStore
import com.example.caloriesdiary.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.datastore.UserPreferences
import com.google.samples.apps.nowinandroid.core.datastore.copy
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                height = it.height,
                weight = it.weight,
                age = it.age,
            )
        }

    suspend fun setHeight(height: Int) {
        userPreferences.updateData {
            it.copy { this.height = height }
        }
    }

    suspend fun setWeight(weight: Int) {
        userPreferences.updateData {
            it.copy { this.weight = weight }
        }
    }

    suspend fun setAge(age: Int) {
        userPreferences.updateData {
            it.copy { this.age = age }
        }
    }
}