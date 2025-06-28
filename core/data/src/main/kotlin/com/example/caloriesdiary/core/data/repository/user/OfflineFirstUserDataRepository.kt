package com.example.caloriesdiary.core.data.repository.user

import com.example.caloriesdiary.core.datastore.UserPreferencesDataSource
import com.example.caloriesdiary.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class OfflineFirstUserDataRepository @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        userPreferencesDataSource.userData

    override suspend fun setHeight(height: Int) {
        userPreferencesDataSource.setHeight(height)
    }

    override suspend fun setWeight(weight: Int) {
        userPreferencesDataSource.setWeight(weight)
    }

    override suspend fun setAge(age: Int) {
        userPreferencesDataSource.setAge(age)
    }
}
