package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_inventory")
data class UserInventory(
    @PrimaryKey(autoGenerate = true)
    val inventoryId: Long = 0,
    val userId: Long,
    val assetId: Long,
    val quantity: Int = 1,
    val isPlaced: Boolean = false,
    val positionX: Float? = null,
    val positionY: Float? = null
)