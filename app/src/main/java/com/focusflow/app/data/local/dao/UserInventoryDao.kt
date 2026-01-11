package com.focusflow.app.data.local.dao

import androidx.room.*
import com.focusflow.app.data.local.entity.UserInventory
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInventoryDao {
    @Insert
    suspend fun addToInventory(item: UserInventory): Long

    @Query("SELECT * FROM user_inventory WHERE userId = :userId")
    fun getUserInventory(userId: Long): Flow<List<UserInventory>>

    @Query("SELECT * FROM user_inventory WHERE userId = :userId AND isPlaced = 1")
    fun getPlacedAssets(userId: Long): Flow<List<UserInventory>>

    @Update
    suspend fun updateInventoryItem(item: UserInventory)

    @Query("DELETE FROM user_inventory WHERE inventoryId = :inventoryId")
    suspend fun removeFromInventory(inventoryId: Long)
}