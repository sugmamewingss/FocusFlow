package com.focusflow.app.data.repository

import com.focusflow.app.data.local.dao.*
import com.focusflow.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FocusFlowRepository(
    private val userDao: UserDao,
    private val sessionDao: FocusSessionDao,
    private val assetDao: VirtualAssetDao,
    private val inventoryDao: UserInventoryDao,
    private val whitelistDao: AppWhitelistDao
) {

    // User operations
    suspend fun createUser(username: String): Long {
        return userDao.insertUser(User(username = username))
    }

    fun getCurrentUser(): Flow<User?> = userDao.getCurrentUser()

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun addCoins(userId: Long, coins: Int) = userDao.addCoins(userId, coins)

    suspend fun addFocusMinutes(userId: Long, minutes: Int) =
        userDao.addFocusMinutes(userId, minutes)

    // Focus session operations
    suspend fun startFocusSession(session: FocusSession): Long {
        return sessionDao.insertSession(session)
    }

    fun getUserSessions(userId: Long): Flow<List<FocusSession>> =
        sessionDao.getUserSessions(userId)

    fun getLastWeekSessions(userId: Long): Flow<List<FocusSession>> =
        sessionDao.getLastWeekSessions(userId)

    suspend fun completeSession(
        sessionId: Long,
        durationMinutes: Int,
        distractions: Int,
        mode: String
    ): Int {
        val session = sessionDao.getSessionById(sessionId) ?: return 0

        // Calculate coins using the formula
        val multiplier = if (mode == "Hard") 1.5 else 1.0
        val coinsEarned = calculateCoins(durationMinutes, multiplier, distractions)

        val updatedSession = session.copy(
            status = "Completed",
            durationMinutes = durationMinutes,
            distractions = distractions,
            coinsEarned = coinsEarned
        )

        sessionDao.insertSession(updatedSession)
        addCoins(session.userId, coinsEarned)
        addFocusMinutes(session.userId, durationMinutes)

        return coinsEarned
    }

    suspend fun failSession(sessionId: Long) {
        val session = sessionDao.getSessionById(sessionId) ?: return
        val updatedSession = session.copy(status = "Failed", coinsEarned = 0)
        sessionDao.insertSession(updatedSession)
    }

    // Virtual assets operations
    fun getAllAssets(): Flow<List<VirtualAsset>> = assetDao.getAllAssets()

    fun getAssetsByType(type: String): Flow<List<VirtualAsset>> =
        assetDao.getAssetsByType(type)

    // Inventory operations
    suspend fun purchaseAsset(userId: Long, assetId: Long): Boolean {
        val user = userDao.getUserById(userId).first() ?: return false
        val assets = assetDao.getAllAssets().first()
        val asset = assets.find { it.assetId == assetId } ?: return false

        if (user.totalZenCoins >= asset.price) {
            inventoryDao.addToInventory(
                UserInventory(userId = userId, assetId = assetId)
            )
            userDao.updateUser(user.copy(totalZenCoins = user.totalZenCoins - asset.price))
            return true
        }
        return false
    }

    fun getUserInventory(userId: Long): Flow<List<UserInventory>> =
        inventoryDao.getUserInventory(userId)

    fun getPlacedAssets(userId: Long): Flow<List<UserInventory>> =
        inventoryDao.getPlacedAssets(userId)

    suspend fun placeAsset(item: UserInventory, x: Float, y: Float) {
        inventoryDao.updateInventoryItem(
            item.copy(isPlaced = true, positionX = x, positionY = y)
        )
    }

    // Whitelist operations
    suspend fun addToWhitelist(userId: Long, packageName: String, appName: String, category: String) {
        whitelistDao.addToWhitelist(
            AppWhitelist(
                userId = userId,
                packageName = packageName,
                appName = appName,
                category = category
            )
        )
    }

    fun getWhitelistApps(userId: Long): Flow<List<AppWhitelist>> =
        whitelistDao.getWhitelistApps(userId)

    suspend fun isAppWhitelisted(userId: Long, packageName: String): Boolean =
        whitelistDao.isAppWhitelisted(userId, packageName)

    // Helper function to calculate coins
    private fun calculateCoins(duration: Int, multiplier: Double, distractions: Int): Int {
        return ((duration * multiplier) / (distractions + 1)).toInt()
    }
}