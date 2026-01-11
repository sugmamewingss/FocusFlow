package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val username: String,
    val totalZenCoins: Int = 0,
    val currentLevel: Int = 1,
    val islandThemeId: Int = 1,
    val totalFocusMinutes: Int = 0
)