package com.example.caloriesdiary.core.data.repository

import com.example.caloriesdiary.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    /**
     * Stream of [UserData].
     */
    val userData: Flow<UserData>

    /**
     * Sets the user's height in cm.
     */
    suspend fun setHeight(height: Int)

    /**
     * Sets the user's weight in kg.
     */
    suspend fun setWeight(weight: Int)

    /**
     * Sets the user's age.
     */
    suspend fun setAge(age: Int)
}
