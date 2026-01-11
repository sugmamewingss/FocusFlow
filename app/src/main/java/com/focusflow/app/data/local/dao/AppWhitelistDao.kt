package com.focusflow.app.data.local.dao

import androidx.room.*
import com.focusflow.app.data.local.entity.AppWhitelist
import kotlinx.coroutines.flow.Flow

@Dao
interface AppWhitelistDao {
    @Insert
    suspend fun addToWhitelist(app: AppWhitelist): Long

    @Query("SELECT * FROM app_whitelist WHERE userId = :userId")
    fun getWhitelistApps(userId: Long): Flow<List<AppWhitelist>>

    @Delete
    suspend fun removeFromWhitelist(app: AppWhitelist)

    @Query("SELECT EXISTS(SELECT 1 FROM app_whitelist WHERE userId = :userId AND packageName = :packageName)")
    suspend fun isAppWhitelisted(userId: Long, packageName: String): Boolean
}