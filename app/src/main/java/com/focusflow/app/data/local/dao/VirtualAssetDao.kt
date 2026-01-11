package com.focusflow.app.data.local.dao

import androidx.room.*
import com.focusflow.app.data.local.entity.VirtualAsset
import kotlinx.coroutines.flow.Flow

@Dao
interface VirtualAssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: VirtualAsset): Long

    @Query("SELECT * FROM virtual_assets")
    fun getAllAssets(): Flow<List<VirtualAsset>>

    @Query("SELECT * FROM virtual_assets WHERE assetType = :type")
    fun getAssetsByType(type: String): Flow<List<VirtualAsset>>
}