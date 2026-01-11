package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_whitelist")
data class AppWhitelist(
    @PrimaryKey(autoGenerate = true)
    val whitelistId: Long = 0,
    val userId: Long,
    val packageName: String,
    val appName: String,
    val category: String
)