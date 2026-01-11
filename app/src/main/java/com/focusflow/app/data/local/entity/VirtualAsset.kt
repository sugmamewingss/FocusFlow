package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "virtual_assets")
data class VirtualAsset(
    @PrimaryKey(autoGenerate = true)
    val assetId: Long = 0,
    val assetName: String,
    val price: Int,
    val assetType: String, // "Flora", "Weather", "Building"
    val iconResource: String,
    val description: String
)